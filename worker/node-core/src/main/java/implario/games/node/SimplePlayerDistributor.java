package implario.games.node;

import implario.games.sdk.Game;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

/**
 * SimplePlayerDistributor assumes that player can only
 * be in one game at any given time
 */
@RequiredArgsConstructor
public class SimplePlayerDistributor implements PlayerDistributor {

    private final GameManager gameManager;

    @Override
    public Game assignGame(UUID playerId) {

        for (Game game : gameManager.getRunningGames().values()) {
            if (game.getAssignedPlayers().contains(playerId))
                return game;
        }

        return null;

    }

}
