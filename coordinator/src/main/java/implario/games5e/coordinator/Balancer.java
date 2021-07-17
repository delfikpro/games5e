package implario.games5e.coordinator;

import java.util.UUID;

public interface Balancer {

    GameNode getSufficientNode(Image image);

    RunningGame getRunningGame(UUID gameId);

    void addNode(GameNode gameNode);

}
