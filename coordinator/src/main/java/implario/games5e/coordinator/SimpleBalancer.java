package implario.games5e.coordinator;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimpleBalancer implements Balancer {

    private final List<GameNode> gameNodes = new ArrayList<>();

    @Override
    public GameNode getSufficientNode(Image image) {
        return null;
    }

    @Override
    public RunningGame getRunningGame(UUID gameId) {
        return null;
    }

    @Override
    public void addNode(GameNode gameNode) {

    }

}
