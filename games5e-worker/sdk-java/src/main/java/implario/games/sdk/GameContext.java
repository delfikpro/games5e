package implario.games.sdk;

import clepto.bukkit.event.EventContext;
import clepto.bukkit.routine.Doer;
import clepto.bukkit.world.Area;
import clepto.cristalix.WorldMeta;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public interface GameContext {

    EventContext getEventContext();

    Doer getDoer();

    Area getMap();

    UUID getGameId();

    List<Player> getPlayers();

    List<UUID> getAssignedPlayers();

    int getLocalGameIndex();

}
