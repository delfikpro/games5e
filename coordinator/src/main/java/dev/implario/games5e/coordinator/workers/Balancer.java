package dev.implario.games5e.coordinator.workers;

import java.util.Collection;
import java.util.UUID;

public interface Balancer {

    Collection<? extends GameNode> getNodes();

    GameNode getSufficientNode(String imageId);

    RunningGame getRunningGame(UUID gameId);

    void addNode(GameNode node);

    void removeNode(GameNode node);
}
