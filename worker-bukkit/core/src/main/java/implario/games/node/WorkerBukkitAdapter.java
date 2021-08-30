package implario.games.node;

import com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.implario.bukkit.event.EventContext;
import implario.games.sdk.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class WorkerBukkitAdapter {

    public static void init(GameNode gameNode) {

        Cache<UUID, Game> joiningPlayers = CacheBuilder
                .newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build();

        EventContext globalContext = new EventContext(e -> true);

        globalContext.on(AsyncPlayerPreLoginEvent.class, EventPriority.LOW, e -> {
            Game game = gameNode.getPlayerDistributor().assignGame(e.getUniqueId());

            if (game == null) {
                e.setKickMessage("No game found for you on this server.");
                e.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
                return;
            }

            joiningPlayers.put(e.getUniqueId(), game);

        });

        try {
            // Games5e aims to support paperless environments
            Class.forName("com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent");
            globalContext.on(PlayerInitialSpawnEvent.class, e -> {

                Game game = gameNode.getPlayerDistributor().assignGame(e.getPlayer().getUniqueId());
                game.getPlayers().add(e.getPlayer());

                Location loc = game.getSpawnLocation(e.getPlayer().getUniqueId());
                e.setSpawnLocation(loc);

            });

            globalContext.on(PlayerSpawnLocationEvent.class, e -> {
                Location loc = gameNode.getGameManager().getGame(e.getPlayer()).getSpawnLocation(e.getPlayer().getUniqueId());
                e.setSpawnLocation(loc);
            });

        } catch (ClassNotFoundException ignored) {

            globalContext.on(PlayerSpawnLocationEvent.class, e -> {

                Game game = gameNode.getPlayerDistributor().assignGame(e.getPlayer().getUniqueId());

                game.getPlayers().add(e.getPlayer());

                Location loc = game.getSpawnLocation(e.getPlayer().getUniqueId());
                e.setSpawnLocation(loc);
            });

        }


        globalContext.on(PlayerRespawnEvent.class, EventPriority.LOWEST, e -> {

            Game game = gameNode.getPlayerDistributor().assignGame(e.getPlayer().getUniqueId());

            Location loc = game.getSpawnLocation(e.getPlayer().getUniqueId());
            e.setRespawnLocation(loc);

        });

        globalContext.on(PlayerJoinEvent.class, e -> {

            e.setJoinMessage("");
            Player player = e.getPlayer();
            UUID uuid = player.getUniqueId();
            Game game = gameNode.getPlayerDistributor().assignGame(uuid);
            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (!game.getPlayers().contains(otherPlayer)) {
                    player.hidePlayer(gameNode, otherPlayer);
                    otherPlayer.hidePlayer(gameNode, player);
                }
            }
        });

        globalContext.on(PlayerQuitEvent.class, e -> e.setQuitMessage(""));

        globalContext.on(PlayerDeathEvent.class, e -> e.setDeathMessage(""));

        globalContext.on(AsyncPlayerChatEvent.class, EventPriority.LOW, e -> {

            UUID uuid = e.getPlayer().getUniqueId();
            Game game = gameNode.getPlayerDistributor().assignGame(uuid);
            e.getRecipients().removeIf(p -> !game.getPlayers().contains(p));

        });

    }

}
