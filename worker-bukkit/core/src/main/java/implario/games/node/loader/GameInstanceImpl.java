package implario.games.node.loader;

import dev.implario.bukkit.event.EventContext;
import implario.games.sdk.GameContext;
import lombok.Data;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

@Data
public class GameInstanceImpl implements GameContext {

    private final EventContext eventContext;
    private final UUID gameId;
    private final List<Player> players;
    private final int localGameIndex;

}
