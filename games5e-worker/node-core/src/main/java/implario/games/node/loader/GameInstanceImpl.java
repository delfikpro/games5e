package implario.games.node.loader;

import clepto.bukkit.event.EventContext;
import clepto.bukkit.routine.Doer;
import clepto.bukkit.world.Area;
import implario.games.sdk.GameContext;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Data
public class GameInstanceImpl implements GameContext {

    private final EventContext eventContext;
    private final Doer doer;
    private final Area map;
    private final UUID gameId;
    private final List<Player> players;
    private final List<UUID> assignedPlayers;
    private final int localGameIndex;

}
