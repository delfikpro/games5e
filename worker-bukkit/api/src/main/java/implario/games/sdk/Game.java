package implario.games.sdk;

import dev.implario.bukkit.event.EventContext;
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

    protected final UUID id;

    protected final List<Player> players;

    protected Game(GameContext context) {

        this.eventContext = context.getEventContext();
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
