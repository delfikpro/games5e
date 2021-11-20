package dev.implario.games5e.coordinator.queue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import dev.implario.games5e.GameInfo;
import dev.implario.games5e.QueueProperties;
import dev.implario.games5e.coordinator.Scheduler;
import dev.implario.games5e.coordinator.workers.GameStarter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class SimpleQueueManager implements QueueManager {

    private Map<UUID, dev.implario.games5e.coordinator.queue.Queue> queueMap;
    private final Scheduler scheduler;
    private final GameStarter starter;
    private final Logger logger = Logger.getLogger("QueueManager");
    private final Path filePath = Paths.get("queues.json");
    private final Gson gson = new Gson();

    @Inject
    @SneakyThrows
    public SimpleQueueManager(Scheduler scheduler, GameStarter starter) {
        this.scheduler = scheduler;
        this.starter = starter;
        if (Files.exists(filePath)) {
            try {
                queueMap = Arrays.stream(gson.fromJson(Files.newBufferedReader(filePath), QueueProperties[].class))
                        .collect(Collectors.toMap(QueueProperties::getQueueId, p -> new Queue(p, getStrategy(p.getStrategy()))));
                logger.info("Initialized " + queueMap.size() + " queues");
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Unable to load queues, using empty", ex);
                queueMap = new HashMap<>();
            }
        } else {
            queueMap = new HashMap<>();
            logger.info("Created new queue map");
        }

        scheduler.repeatEvery(500, () -> {
            logger.info("Updating queues");
            for (dev.implario.games5e.coordinator.queue.Queue queue : queueMap.values()) {
                QueueProperties properties = queue.getProperties();
                if (queue.getProperties() == null) continue;

                List<dev.implario.games5e.coordinator.queue.Queue.Emission> emissions = queue.getStrategy().tryEmitMax(queue);

                String queueName = properties.getQueueId() + " for " + properties.getImageId();
                for (dev.implario.games5e.coordinator.queue.Queue.Emission emission : emissions) {
                    logger.info("Got an emission from queue " + queueName);
                    starter.startGame(new GameInfo(UUID.randomUUID(), new UUID(0xC0DEC0DEC0DEC0DEL, 0xC0DEC0DEC0DEC0DEL),
                                    properties.getImageId(), System.currentTimeMillis(), gson.toJsonTree(emission.mergePreferences())))
                            .whenComplete((game, t) -> {
                                if (t != null) {
                                    logger.log(Level.SEVERE, "Unable to start game by queue " + queueName, t);
                                } else {
                                    logger.info("Game started successfully: " + game);
                                }
                            });
                }
            }
        });
    }

    public void save() {
        try {
            gson.toJson(queueMap, Files.newBufferedWriter(filePath));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Unable to save queues", ex);
        }
    }

    @Override
    public dev.implario.games5e.coordinator.queue.Queue getQueue(UUID queueId) {
        return queueMap.get(queueId);
    }

    @Override
    public void addQueue(dev.implario.games5e.coordinator.queue.Queue queue) {
        queueMap.put(queue.getProperties().getQueueId(), queue);
        scheduler.runAfter(1000, this::save);
    }

    @Override
    public void removeQueue(UUID queueId) {
        queueMap.remove(queueId);
        scheduler.runAfter(1000, this::save);
    }

    @Override
    public QueueStrategy getStrategy(String name) {
        switch (name) {
            case "simple":
                return new SimpleLaxQueueStrategy(0);
            case "lax":
                return new SimpleLaxQueueStrategy(0.1);
        }
        throw new IllegalArgumentException("No strategy for string " + name);
    }

    @Override
    public Collection<Queue> getQueues() {
        return queueMap.values();
    }
}
