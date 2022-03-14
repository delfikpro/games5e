package dev.implario.games5e.node;

import dev.implario.bukkit.event.EventContext;
import dev.implario.bukkit.routine.Routine;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Delegate;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.*;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Game implements PlayerFilter {

    public static final EventContext GLOBAL_CONTEXT = new EventContext(anything -> false);

    @Delegate
    protected final EventContext context = GLOBAL_CONTEXT.fork();

    protected final UUID id;

    protected final Set<Player> players = new HashSet<>();

    protected final List<World> worlds = new ArrayList<>();

    protected final Map<String, String> meta = new HashMap<>();

    private final Routine ticker = context.every(1, Routine.EMPTY_ACTION);

    private final long initTimestamp = System.currentTimeMillis();

    @Setter
    private boolean terminated;

    @Setter
    private long emptySince;

    public abstract Location getSpawnLocation(UUID uuid);

    public void broadcast(String... messages) {
        for (Player player : players) {
            player.sendMessage(messages);
        }
    }

    public void onTerminate(Consumer<Routine> action) {
        ticker.onKill(action);
    }

}
