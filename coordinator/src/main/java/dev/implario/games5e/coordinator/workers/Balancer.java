package dev.implario.games5e.coordinator.workers;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface Balancer {

    Collection<? extends GameNode> getNodes();

    CompletableFuture<GameNode> getSufficientNode(String imageId);

    RunningGame getRunningGame(UUID gameId);

    void addNode(GameNode node);

    void removeNode(GameNode node);
}
