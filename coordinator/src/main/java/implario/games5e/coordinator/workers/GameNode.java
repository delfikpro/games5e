package implario.games5e.coordinator.workers;

import dev.implario.nettier.NettierRemote;
import implario.games5e.GameInfo;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;

public interface GameNode {

    boolean isImageSupported(String imageId);

    NettierRemote getRemote();

    Collection<? extends RunningGame> getRunningGames();

    CompletableFuture<RunningGame> startGame(GameInfo info);

}
