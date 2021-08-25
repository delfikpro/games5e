package implario.games.sdk;

import clepto.bukkit.event.EventContext;
import clepto.bukkit.routine.Doer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface GameContext {

    EventContext getEventContext();

    Doer getDoer();

    UUID getGameId();

    List<Player> getPlayers();

    int getLocalGameIndex();

}
