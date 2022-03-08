package dev.implario.games5e.coordinator.workers;

import com.google.inject.Inject;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SimpleBalancer implements Balancer {

    @Getter
    private final List<GameNode> nodes = new ArrayList<>();

    private final Map<String, CompletableFuture<GameNode>> awaitGameNodeMap = new ConcurrentHashMap<>();

    private final GameNodeStarter nodeStarter;

    private final Logger logger = Logger.getLogger("SimpleBalancer");

    @Override
    @SneakyThrows
    public CompletableFuture<GameNode> getSufficientNode(String imageId) {
        GameNode sufficientNode = nodes.stream()
                .filter(node -> node.isImageSupported(imageId) && node.canCreateGame())
                .min(Comparator.comparingInt(node -> node.getRunningGames().size()))
                .orElse(null);

        if (sufficientNode == null) {
            UUID newId = nodeStarter.createGameNode(imageId);

            CompletableFuture<GameNode> future = new CompletableFuture<>();

            awaitGameNodeMap.put(imageId, future);

            return future.whenComplete((node, __) -> {
                node.setId(newId);

                logger.info("Node created with id " + newId);
            });
        }

        return CompletableFuture.completedFuture(sufficientNode);
    }

    @Override
    public RunningGame getRunningGame(UUID gameId) {
        for (GameNode gameNode : nodes) {
            for (RunningGame game : gameNode.getRunningGames()) {
                if (game.getInfo().getGameId().equals(gameId)) {
                    return game;
                }
            }
        }
        return null;
    }

    @Override
    public void addNode(GameNode node) {
        nodes.add(node);

        awaitGameNodeMap.forEach((key, value) -> {
            if (node.isImageSupported(key)) {
                value.complete(node);
            }
        });
    }

    @Override
    public void removeNode(GameNode node) {
        nodes.remove(node);
    }

}
