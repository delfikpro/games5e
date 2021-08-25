package implario.games.sdk;

import clepto.bukkit.event.EventContext;
import clepto.bukkit.routine.Doer;
import lombok.Getter;
import lombok.experimental.Delegate;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public abstract class Game {

    @Delegate
    protected final EventContext eventContext;

    @Delegate
    protected final Doer doer;

    protected final UUID id;

    protected final List<Player> players;

    protected Game(GameContext context) {

        this.eventContext = context.getEventContext();
        this.doer = context.getDoer();
        this.id = context.getGameId();
        this.players = new ArrayList<>();

    }

    public abstract Location getSpawnLocation(UUID uuid);

    public void broadcast(String... messages) {
        for (Player player : players) {
            player.sendMessage(messages);
        }
    }

}
