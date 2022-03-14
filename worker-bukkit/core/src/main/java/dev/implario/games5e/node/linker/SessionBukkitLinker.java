package dev.implario.games5e.node.linker;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.implario.bukkit.event.EventContext;
import dev.implario.games5e.node.DefaultGameNode;
import dev.implario.games5e.node.Game;
import lombok.Getter;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Getter
public class SessionBukkitLinker implements BukkitLinker {

    public static SessionBukkitLinker link(DefaultGameNode node) {
        SessionBukkitLinker linker = new SessionBukkitLinker();
        EventContext context = linker.getContext();

        context.on(AsyncPlayerPreLoginEvent.class, EventPriority.LOW, event -> {
            for (Game game : node.getRunningGames().values()) {
                if (game.acceptPlayer(event)) {
                    linker.getJoiningPlayers().put(new IdentityUUID(event.getUniqueId()), game);
                    System.out.println("Assigned player " + event.getUniqueId() + " to game " + game.getId());
                    return;
                }
            }

            event.setKickMessage("No game found for you on this server.");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);

        });
        BukkitLinkers.handleSpawnLocations(linker, context,
                (game, player) -> linker.playerMap.put(player.getUniqueId(), game),
                (game, player) -> linker.playerMap.remove(player.getUniqueId(), game)
        );
        BukkitLinkers.isolateChat(linker, context);
        BukkitLinkers.isolatePlayerVisibility(linker, context);
        BukkitLinkers.disableDeathMessages(context);
        BukkitLinkers.disableJoinQuitMessages(context);
        BukkitLinkers.scheduleStaleGamesCleanup(node, linker, context, 60);

        return linker;
    }

    private final EventContext context = new EventContext(anything -> true);

    private final Cache<IdentityUUID, Game> joiningPlayers = CacheBuilder.newBuilder()
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();

    private final Map<UUID, Game> playerMap = new IdentityHashMap<>();

    protected SessionBukkitLinker() { }

    @Override
    public Game getGameByPlayerId(UUID playerId) {
        Game game = playerMap.get(playerId);
        if (game != null) {
            return game;
        } else {
            return joiningPlayers.getIfPresent(new IdentityUUID(playerId));
        }
    }
}
