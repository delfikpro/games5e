package dev.implario.games5e.coordinator.workers;

import dev.implario.games5e.GameInfo;
import dev.implario.nettier.NettierRemote;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GameNode {

    boolean canCreateGame();

    void setCanCreateGame(boolean canCreateGame);

    void destroy();

    UUID getId();

    void setId(UUID id);

    boolean isImageSupported(String imageId);

    NettierRemote getRemote();

    Collection<? extends RunningGame> getRunningGames();

    CompletableFuture<RunningGame> startGame(GameInfo info);

    void removeGame(UUID uuid);

    boolean isListeningQueues();

    void setListeningQueues(boolean listeningQueues);

}
