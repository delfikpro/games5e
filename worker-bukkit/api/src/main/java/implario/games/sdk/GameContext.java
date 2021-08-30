package implario.games.sdk;

import dev.implario.bukkit.event.EventContext;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface GameContext {

    EventContext getEventContext();

    UUID getGameId();

    List<Player> getPlayers();

    int getLocalGameIndex();

}
