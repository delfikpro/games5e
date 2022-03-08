package dev.implario.games5e.coordinator;

import com.google.inject.Inject;
import dev.implario.games5e.Games5eGameState;
import dev.implario.games5e.coordinator.queue.Queue;
import dev.implario.games5e.coordinator.queue.QueueManager;
import dev.implario.games5e.coordinator.workers.*;
import dev.implario.games5e.packets.*;
import dev.implario.nettier.Nettier;
import dev.implario.nettier.NettierServer;
import dev.implario.nettier.RemoteException;
import implario.LoggerUtils;
import dev.implario.games5e.coordinator.queue.Party;
import dev.implario.games5e.coordinator.queue.QueueStrategy;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class CoordinatorEndpoint {

    private final Balancer balancer;
    private final GameStarter starter;
    private final QueueManager queueManager;
    private final Scheduler scheduler;

    public void start(int port) {
        Logger logger = LoggerUtils.simpleLogger("games5e-coordinator");

        NettierServer server = Nettier.createServer();
        server.setExecutor(scheduler::run);

        scheduler.repeatEvery(1000, () -> {
            for (GameNode node : balancer.getNodes()) {
                if (!node.canCreateGame() && node.getRunningGames().size() == 0)
                    node.destroy();
            }
        });

        server.setPacketTranslator((packet, expectedType) -> {
            if (packet instanceof PacketError) {
                throw new RemoteException(RemoteException.ErrorLevel.SEVERE, ((PacketError) packet).getMessage());
            }
            return packet;
        });

        server.addListener(PacketCreateGame.class, (talk, packet) -> {

            val future = starter.startGame(packet.getGameInfo());

            if (!future.isCompletedExceptionally()) {
                talk.respond(new PacketOk("Game created and is now initializing"));
            }

            future.whenComplete((game, exception) -> {
                if (exception != null) {
                    exception.printStackTrace();
                    talk.respond(new PacketError("Error starting game: " + exception.getMessage()));
                } else {
                    talk.respond(new PacketGameStatus(game.getInfo(), game.getMeta(), game.getState()));
                }
            });

        });

        server.addListener(PacketGameStatus.class, (talk, packet) -> {
            GameNode gameNode = balancer.getNodes().stream().filter(node -> node.getRemote() == talk.getRemote()).findFirst().orElse(null);
            if (gameNode == null) {
                talk.respond(new PacketError("Not authorized"));
                return;
            }

            if (packet.getState() == Games5eGameState.TERMINATED) {
                gameNode.removeGame(packet.getInfo().getGameId());
            }
        });

        server.addListener(PacketRequestGameStatus.class, (talk, packet) -> {

            RunningGame runningGame = balancer.getRunningGame(packet.getGameId());

            // ToDo: respond with TERMINATED state for games that existed in the past
            if (runningGame == null) talk.respond(new PacketError("No such game"));
            else
                talk.respond(new PacketGameStatus(runningGame.getInfo(), runningGame.getMeta(), runningGame.getState()));

        });

        server.addListener(PacketUpdateMeta.class, (talk, packet) -> {
            RunningGame runningGame = balancer.getRunningGame(packet.getGameId());
            if (runningGame == null) {
                talk.respond(new PacketError("No such game"));
                return;
            }
            runningGame.getMeta().putAll(packet.getMeta());
            talk.respond(new PacketOk("Updated"));
        });

        server.addListener(PacketQueueSetup.class, (talk, packet) -> {
            UUID queueId = packet.getQueueId();
            dev.implario.games5e.coordinator.queue.Queue queue = queueManager.getQueue(queueId);
            boolean create = queue == null;
            QueueStrategy strategy = queueManager.getStrategy(packet.getProperties().getStrategy());
            if (create) {
                queue = new dev.implario.games5e.coordinator.queue.Queue(packet.getProperties(), strategy);
                queueManager.addQueue(queue);
                talk.respond("Queue created");
            } else {
                queue.clear();
                queue.setProperties(packet.getProperties());
                queue.setStrategy(strategy);
                talk.respond("Queue edited");
            }
        });

        server.addListener(PacketQueueRemove.class, (talk, packet) -> {
            UUID queueId = packet.getQueueId();
            if (queueManager.getQueue(queueId) == null) {
                talk.respond(new PacketError("No queue with id " + queueId));
                return;
            }
            queueManager.removeQueue(queueId);
            talk.respond(new PacketOk("Queue deleted"));
        });

        server.addListener(PacketQueueEnter.class, (talk, packet) -> {
            UUID queueId = packet.getQueueId();
            dev.implario.games5e.coordinator.queue.Queue queue = queueManager.getQueue(queueId);
            if (queue == null) {
                talk.respond(new PacketError("No queue with id " + queueId));
                return;
            }
            for (Queue otherQueue : queueManager.getQueues()) {
                otherQueue.removePlayers(packet.getParty());
            }
            Party party = new Party(packet.getParty(), packet.getBannedOptions(),
                    packet.isAllowSplit(), packet.isAllowExtra());
            queue.addParty(party);
            talk.respond(new PacketOk("OK"));
        });

        server.addListener(PacketQueueLeave.class, (talk, packet) -> {
            for (Queue otherQueue : queueManager.getQueues()) {
                otherQueue.removePlayers(packet.getPlayers());
            }
            talk.respond(new PacketOk("OK"));
        });

        server.addListener(PacketImageUpdate.class, (talk, packet) -> balancer.getNodes().forEach(x -> {
            if (x.isImageSupported(packet.getImageId()))
                x.setCanCreateGame(false);
        }));

        server.addListener(PacketNodeHandshakeV1.class, (talk, packet) -> {

            // ToDo: validate token
            // ToDo: reject packet if already authorized

            // ToDo: restore running games from packet.activeGames
            List<RunningGame> runningGames = new ArrayList<>();

            List<String> supportedImagePrefixes = packet.getSupportedImagePrefixes();
            logger.info("New node: " + talk.getRemote().getAddress() + ", supported image prefixes: " + supportedImagePrefixes);
            GameNodeImpl node = new GameNodeImpl(talk.getRemote(), runningGames, s -> {
                for (String prefix : supportedImagePrefixes) {
                    System.out.println("Checking prefix '" + prefix + "' with s = '" + s + "'");
                    if (s.startsWith(prefix)) return true;
                }
                return false;
            }, balancer);

            node.getRemote().send(new PacketAllQueueStates(queueManager.getQueues().stream()
                    .map(q -> new PacketQueueState(q.getProperties(), q.getParties().stream()
                            .map(Party::getList)
                            .collect(Collectors.toList()))).collect(Collectors.toList())));

            balancer.addNode(node);
            talk.getRemote().setDisconnectHandler(() -> balancer.removeNode(node));

        });

        // Subscription to queue updates
        server.addListener(PacketListenQueues.class, (talk, packet) -> {
            for (GameNode node : balancer.getNodes()) {
                if (node.getRemote() == talk.getRemote()) {
                    node.setListeningQueues(true);
                }
            }
        });

        server.start(port);
        logger.info("Started on port :" + port);

    }

}
