package dev.implario.games5e.node.linker;

import com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import dev.implario.bukkit.event.EventContext;
import dev.implario.games5e.node.DefaultGameNode;
import dev.implario.games5e.node.Game;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

public class WorkerBukkitAdapter {

    @Getter
    private final Cache<IdentityUUID, Game> joiningPlayers = CacheBuilder.newBuilder()
            .expireAfterWrite(15, TimeUnit.SECONDS)
            .build();

    @Getter
    private final Map<UUID, Game> playerMap = new ConcurrentHashMap<>();

    public Game getGameByPlayer(Player player) {
        return getGameByPlayerId(player.getUniqueId());
    }

    public Game getGameByPlayerId(UUID uuid) {
        Game game = playerMap.get(uuid);
        if (game != null) return game;
        return joiningPlayers.getIfPresent(new IdentityUUID(uuid));
    }

    public void init(DefaultGameNode gameNode) {

        EventContext globalContext = new EventContext(e -> true);


        globalContext.on(AsyncPlayerPreLoginEvent.class, EventPriority.LOW, event -> {
            for (Game game : gameNode.getRunningGames().values()) {
                if (game.acceptPlayer(event)) {
                    joiningPlayers.put(new IdentityUUID(event.getUniqueId()), game);
                    System.out.println("Assigned player " + event.getUniqueId() + " to game " + game.getId());
                    return;
                }
            }

            event.setKickMessage("No game found for you on this server.");
            event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);

        });

        // We are trying to add player in Game#players list as early as possible
        // But PlayerLoginEvent doesn't fit us 'cause it can be called in a separate tick
        // from all other events and result in an undefined behaviour when trying to
        // operate on players from Game#getPlayers.
        // So we use spawn location events as they are the only meaningful thing to listen
        // in context of a game.
        try {
            // Games5e aims to support paperless environments
            Class.forName("com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent");
            globalContext.on(PlayerInitialSpawnEvent.class, e -> {

                Game game = getGameByPlayer(e.getPlayer());
                game.getPlayers().add(e.getPlayer());

                Location loc = game.getSpawnLocation(e.getPlayer().getUniqueId());
                e.setSpawnLocation(loc);

            });

            globalContext.on(PlayerSpawnLocationEvent.class, e -> {
                Location loc = getGameByPlayer(e.getPlayer()).getSpawnLocation(e.getPlayer().getUniqueId());
                e.setSpawnLocation(loc);
            });

        } catch (ClassNotFoundException ignored) {

            globalContext.on(PlayerSpawnLocationEvent.class, e -> {

                Player player = e.getPlayer();
                Game game = getGameByPlayer(player);
                game.getPlayers().add(player);

                Location loc = game.getSpawnLocation(player.getUniqueId());
                e.setSpawnLocation(loc);
            });

        }


        globalContext.on(PlayerRespawnEvent.class, EventPriority.LOWEST, e -> {

            Game game = getGameByPlayer(e.getPlayer());
            Location loc = game.getSpawnLocation(e.getPlayer().getUniqueId());
            e.setRespawnLocation(loc);

        });

        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(WorkerBukkitAdapter.class);

        globalContext.on(PlayerJoinEvent.class, e -> {

            e.setJoinMessage("");
            Player player = e.getPlayer();
            Game game = getGameByPlayer(player);

            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (!game.getPlayers().contains(otherPlayer)) {
                    player.hidePlayer(plugin, otherPlayer);
                    otherPlayer.hidePlayer(plugin, player);
                }
            }
        });

        globalContext.on(PlayerQuitEvent.class, e -> e.setQuitMessage(""));

        globalContext.on(PlayerDeathEvent.class, e -> e.setDeathMessage(""));

        globalContext.on(AsyncPlayerChatEvent.class, EventPriority.LOW, e -> {

            UUID uuid = e.getPlayer().getUniqueId();
            Game game = getGameByPlayerId(uuid);
            e.getRecipients().removeIf(p -> !game.getPlayers().contains(p));

        });

        // Clean up stale games
        globalContext.every(1, r -> {
            long time = System.currentTimeMillis();
            List<UUID> toRemove = new ArrayList<>();
            for (Game game : gameNode.getRunningGames().values()) {
                if (game.getPlayers().stream().noneMatch(Player::isOnline)) {
                    if (game.getEmptySince() != 0 && game.getEmptySince() - time > 60000) {
                        toRemove.add(game.getId());
                    }
                    game.setEmptySince(time);
                } else {
                    game.setEmptySince(0);
                }
            }
            for (UUID uuid : toRemove) {
                Game game = gameNode.getGameByGameId(uuid);
                gameNode.getRunningGames().remove(uuid);
                game.getContext().unregisterAll();
            }
        });

    }

}
