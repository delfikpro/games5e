package dev.implario.games5e.node.linker;

import com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent;
import dev.implario.bukkit.event.EventContext;
import dev.implario.games5e.node.DefaultGameNode;
import dev.implario.games5e.node.Game;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.event.player.PlayerSpawnLocationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;

public class BukkitLinkers {

    public static void scheduleStaleGamesCleanup(DefaultGameNode node, BukkitLinker linker, EventContext context, int secondsEmpty) {
        context.every(1, r -> {
            long time = System.currentTimeMillis();
            List<UUID> toRemove = new ArrayList<>();
            for (Game game : node.getRunningGames().values()) {
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
                Game game = node.getGameByGameId(uuid);
                node.getRunningGames().remove(uuid);
                game.getContext().unregisterAll();
            }
        });
    }

    public static void isolateChat(BukkitLinker linker, EventContext context) {
        context.on(AsyncPlayerChatEvent.class, EventPriority.LOW, e -> {

            UUID uuid = e.getPlayer().getUniqueId();
            Game game = linker.getGameByPlayerId(uuid);
            e.getRecipients().removeIf(p -> !game.getPlayers().contains(p));

        });
    }

    public static void disableJoinQuitMessages(EventContext context) {
        context.on(PlayerQuitEvent.class, e -> e.setQuitMessage(""));
        context.on(PlayerJoinEvent.class, e -> e.setJoinMessage(""));
    }

    public static void disableDeathMessages(EventContext context) {
        context.on(PlayerDeathEvent.class, e -> e.setDeathMessage(""));
    }

    public static void isolatePlayerVisibility(BukkitLinker linker, EventContext context) {
        JavaPlugin plugin = JavaPlugin.getProvidingPlugin(WorkerBukkitAdapter.class);

        context.on(PlayerJoinEvent.class, e -> {

            Player player = e.getPlayer();
            Game game = linker.getGameByPlayer(player);

            for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
                if (!game.getPlayers().contains(otherPlayer)) {
                    player.hidePlayer(plugin, otherPlayer);
                    otherPlayer.hidePlayer(plugin, player);
                }
            }
        });
    }

    public static void handleSpawnLocations(BukkitLinker linker, EventContext context,
                                            BiConsumer<Game, Player> spawnHandler) {

        // We are trying to add player in Game#players list as early as possible
        // But PlayerLoginEvent doesn't fit us 'cause it can be called in a separate tick
        // from all other events and result in an undefined behaviour when trying to
        // operate on players from Game#getPlayers.
        // So we use spawn location events as they are the only meaningful thing to listen
        // in context of a game.
        try {
            // Games5e aims to support paperless environments
            Class.forName("com.destroystokyo.paper.event.player.PlayerInitialSpawnEvent");
            context.on(PlayerInitialSpawnEvent.class, e -> {

                Player player = e.getPlayer();
                Game game = linker.getGameByPlayer(player);
                game.getPlayers().add(player);
                spawnHandler.accept(game, player);

                Location loc = game.getSpawnLocation(player.getUniqueId());
                e.setSpawnLocation(loc);

            });

            context.on(PlayerSpawnLocationEvent.class, e -> {
                Location loc = linker.getGameByPlayer(e.getPlayer()).getSpawnLocation(e.getPlayer().getUniqueId());
                e.setSpawnLocation(loc);
            });

        } catch (ClassNotFoundException ignored) {

            context.on(PlayerSpawnLocationEvent.class, e -> {

                Player player = e.getPlayer();
                Game game = linker.getGameByPlayer(player);
                game.getPlayers().add(player);
                spawnHandler.accept(game, player);

                Location loc = game.getSpawnLocation(player.getUniqueId());
                e.setSpawnLocation(loc);
            });

        }

        context.on(PlayerRespawnEvent.class, EventPriority.LOWEST, e -> {

            UUID playerId = e.getPlayer().getUniqueId();
            Game game = linker.getGameByPlayerId(playerId);
            Location loc = game.getSpawnLocation(playerId);
            e.setRespawnLocation(loc);

        });

        context.on(PlayerQuitEvent.class, EventPriority.HIGHEST, e -> {
            UUID uuid = e.getPlayer().getUniqueId();
            Game game = linker.getGameByPlayerId(uuid);
            if (game != null) {
                game.getPlayers().remove(e.getPlayer());
            }
        });

    }

}
