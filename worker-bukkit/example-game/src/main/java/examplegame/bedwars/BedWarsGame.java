package examplegame.bedwars;

import implario.games.sdk.*;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.*;

public class BedWarsGame extends Game {

    public static final int GAME_DURATION = 60;

    private final List<Location> playerBlocks = new ArrayList<>();

    private final Map<UUID, Team> teams = new HashMap<>();

    public BedWarsGame(GameContext context, BedWarsSetupData data) {
        super(context);

        WorldCreator worldCreator = new WorldCreator("tmp/" + UUID.randomUUID());
        worldCreator.type(WorldType.FLAT);
        worldCreator.generatorSettings("3;1*minecraft:air;minecraft:plains;");
        worldCreator.createWorld();

        getEventContext().on(PlayerJoinEvent.class, EventPriority.NORMAL, event -> {

            Player player = event.getPlayer();
            BedWarsSetupData.TeamData teamData = data.getTeamByPlayer(player.getUniqueId());

            Team team = teams.get(teamData.getTeamId());
            if (team == null) return;

            team.getPlayers().add(player);

            player.sendMessage("Вы зашли за команду " + team.getColor());

            for (Team t : teams.values()) {
                if (t.getPlayers().size() < t.getAssignedPlayers().size()) return;
            }

            start();

        });

    }

    @Override
    public Location getSpawnLocation(UUID uuid) {
        for (Team team : teams.values()) {
            if (team.getAssignedPlayers().contains(uuid))
                return team.getSpawn();
        }
        return null;
    }

    public void start() {

        getDoer().every(1).seconds(routine -> {

            int secondsLeft = GAME_DURATION - routine.getPass();

            if (secondsLeft % 10 == 0)
                broadcast("До конца игры осталось " + secondsLeft + " секунд.");

            if (secondsLeft == 0) {

//                end(new GameResults());
            }

        });

        getEventContext().on(BlockPlaceEvent.class, EventPriority.MONITOR, e -> {
            if (!e.isCancelled())
                playerBlocks.add(e.getBlock().getLocation());
        });

        getEventContext().on(BlockBreakEvent.class, e -> {

            Location blockLoc = e.getBlock().getLocation();

            if (e.getBlock().getType() == Material.BED_BLOCK) {

                Team bedOwner = Collections.min(teams.values(), Comparator.comparingDouble(team -> team.getSpawn().distanceSquared(blockLoc)));

                if (!bedOwner.isBedAlive()) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("§cКровать команды §f" + bedOwner.getName() + " §cуже сломана.");
                    return;
                }

                e.setDropItems(false);

                broadcast("§e" + e.getPlayer().getDisplayName() + " §fразрушил кровать команды §e" + bedOwner.getName());
                bedOwner.setBedAlive(false);

            } else {
                if (!playerBlocks.remove(blockLoc) && e.getPlayer().getGameMode() != GameMode.CREATIVE)
                    e.setCancelled(true);
            }

        });

    }

}
