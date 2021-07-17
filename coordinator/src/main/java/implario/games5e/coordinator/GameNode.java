package implario.games5e.coordinator;

import com.google.gson.JsonElement;
import dev.implario.nettier.NettierRemote;
import implario.games5e.GameInfo;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface GameNode {

    NettierRemote getRemote();

    Collection<? extends RunningGame> getRunningGames();

    void startGame(GameInfo info, Image image);

}
