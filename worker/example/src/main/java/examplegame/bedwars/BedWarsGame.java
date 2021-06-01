package examplegame.bedwars;

import clepto.bukkit.world.Label;
import implario.ListUtils;
import implario.games.sdk.*;
import org.bukkit.*;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.*;

public class BedWarsGame extends Game {

    public static final int GAME_DURATION = 60;

    private final List<Location> playerBlocks = new ArrayList<>();

    private final List<Team> teams = new ArrayList<>();

    public BedWarsGame(GameContext context, BedWarsSetupData data) {
        super(context);




        for (BedWarsSetupData.TeamData teamData : data.getTeams()) {

            DyeColor color = teamData.getColor();
            String name = "§" + "0123456789abcdef".charAt(color.ordinal()) + color.name();
            List<Label> spawns = getMap().getLabels("spawn", color.name().toLowerCase(Locale.ROOT));
            Team team = new Team(color, name, spawns, teamData.getAssignedPlayers());
            teams.add(team);

        }

    }

    @Override
    public Location getSpawnLocation(UUID uuid) {
        for (Team team : teams) {
            if (team.getAssignedPlayers().contains(uuid))
                return ListUtils.random(team.getRespawns());
        }
        return super.getSpawnLocation(uuid);
    }

    @Override
    public void start() {

        getDoer().every(1).seconds(routine -> {

            int secondsLeft = GAME_DURATION - routine.getPass();

            if (secondsLeft % 10 == 0)
                broadcast("До конца игры осталось " + secondsLeft + " секунд.");

            if (secondsLeft == 0) {
                end(new GameResults());
            }

        });

        getEventContext().on(BlockPlaceEvent.class, EventPriority.MONITOR, e -> {
            if (!e.isCancelled())
                playerBlocks.add(e.getBlock().getLocation());
        });

        getEventContext().on(BlockBreakEvent.class, e -> {

            Location blockLoc = e.getBlock().getLocation();

            if (e.getBlock().getType() == Material.BED_BLOCK) {

                Team bedOwner = Collections.min(teams, Comparator.comparingDouble(team -> team.getRespawns().get(0).distanceSquared(blockLoc)));

                if (!bedOwner.isBedAlive()) {
                    e.setCancelled(true);
                    e.getPlayer().sendMessage("§cКровать команды §f" + bedOwner.getName() + " §cуже сломана.");
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
