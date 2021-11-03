package implario.games.node.linker;

import implario.games.node.Game;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface BukkitLinker {

    Game getGameByPlayerId(UUID playerId);

    default Game getGameByPlayer(Player player) {
        return this.getGameByPlayerId(player.getUniqueId());
    }

}
