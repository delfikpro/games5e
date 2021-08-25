package implario.games5e.coordinator;

import com.google.gson.JsonElement;
import dev.implario.nettier.NettierRemote;
import implario.games5e.GameInfo;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GameNode {

    boolean isImageSupported(String imageId);

    NettierRemote getRemote();

    Collection<? extends RunningGame> getRunningGames();

    CompletableFuture<Void> startGame(GameInfo info, String imageId);

}
