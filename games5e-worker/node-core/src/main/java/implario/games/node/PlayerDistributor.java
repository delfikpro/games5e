package implario.games.node;

import com.destroystokyo.paper.profile.PlayerProfile;
import implario.games.sdk.Game;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.UUID;

public interface PlayerDistributor {

    Game assignGame(UUID playerId);

}
