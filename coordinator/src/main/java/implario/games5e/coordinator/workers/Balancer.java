package implario.games5e.coordinator.workers;

import java.util.UUID;

public interface Balancer {

    GameNode getSufficientNode(String imageId);

    RunningGame getRunningGame(UUID gameId);

    void addNode(GameNode node);

    void removeNode(GameNode node);
}
