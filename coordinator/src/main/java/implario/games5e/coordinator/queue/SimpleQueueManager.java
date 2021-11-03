package implario.games5e.coordinator.queue;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import implario.games5e.GameInfo;
import implario.games5e.QueueProperties;
import implario.games5e.coordinator.Scheduler;
import implario.games5e.coordinator.workers.GameStarter;
import lombok.SneakyThrows;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleQueueManager implements QueueManager {

    private Map<UUID, Queue> queueMap;
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
                queueMap = gson.fromJson(Files.newBufferedReader(filePath), new TypeToken<Map<UUID, Queue>>() {}.getType());
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
            for (Queue queue : queueMap.values()) {
                QueueProperties properties = queue.getProperties();
                if (queue.getProperties() == null) continue;
                String queueName = properties.getQueueId() + " for " + properties.getImageId();
                for (Queue.Emission emission : queue.recommendedWalk()) {
                    logger.info("Got an emission from queue " + queueName);
                    starter.startGame(new GameInfo(UUID.randomUUID(), new UUID(0xC0DEC0DEC0DEC0DEL, 0xC0DEC0DEC0DEC0DEL),
                                    properties.getImageId(), System.currentTimeMillis(), gson.toJsonTree(emission.getPreferences())))
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
    public Queue getQueue(UUID queueId) {
        return queueMap.get(queueId);
    }

    @Override
    public Queue createQueue(UUID queueId) {
        Queue queue = new Queue();
        queueMap.put(queueId, queue);
        scheduler.runAfter(1000, this::save);
        return queue;
    }

    @Override
    public void deleteQueue(UUID queueId) {
        queueMap.remove(queueId);
        scheduler.runAfter(1000, this::save);
    }
}
