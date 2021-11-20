package dev.implario.games5e.coordinator.workers;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class SimpleBalancer implements Balancer {

    @Getter
    private final List<GameNode> nodes = new ArrayList<>();

    @Override
    public GameNode getSufficientNode(String imageId) {
        return nodes.stream()
                .filter(node -> node.isImageSupported(imageId))
                .min(Comparator.comparingInt(node -> node.getRunningGames().size()))
                .orElse(null);
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
    }

    @Override
    public void removeNode(GameNode node) {
        nodes.remove(node);
    }

}
