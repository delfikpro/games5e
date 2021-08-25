package implario.games.node.loader;

import com.google.gson.JsonElement;
import implario.games.sdk.Game;
import implario.games.sdk.GameContext;

public interface GameProvider {

    Game provide(GameContext instance, JsonElement gameSettings) throws BadImageException;

}
