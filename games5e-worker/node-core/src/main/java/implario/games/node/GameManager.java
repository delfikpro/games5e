package implario.games.node;

import com.google.gson.JsonElement;
import implario.games.node.loader.BadImageException;
import implario.games.node.loader.GameImage;
import implario.games.sdk.Game;
import implario.games.sdk.GameContext;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class GameManager {

    @Getter
    private final Map<UUID, Game> runningGames = new HashMap<>();

    public Game createGame(GameImage image, GameContext instance, JsonElement gameSettings) throws BadImageException {

        Game game = image.getGameProvider().provide(instance, gameSettings);
        runningGames.put(game.getGameId(), game);
        return game;

    }

    public Game getGame(UUID gameId) {
        return runningGames.get(gameId);
    }

    public Game getGame(Player player) {
        for (Game game : runningGames.values()) {
            for (Player p : game.getPlayers()) {
                if (p == player.getPlayer()) return game;
            }
        }
        return null;
    }

}
