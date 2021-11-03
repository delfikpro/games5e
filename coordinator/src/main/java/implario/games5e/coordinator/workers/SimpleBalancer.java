package implario.games5e.coordinator.workers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimpleBalancer implements Balancer {

    private final List<GameNode> gameNodes = new ArrayList<>();

    @Override
    public GameNode getSufficientNode(String imageId) {
        return gameNodes.stream()
                .filter(node -> node.isImageSupported(imageId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public RunningGame getRunningGame(UUID gameId) {
        for (GameNode gameNode : gameNodes) {
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
        gameNodes.add(node);
    }

    @Override
    public void removeNode(GameNode node) {
        gameNodes.remove(node);
    }

}
