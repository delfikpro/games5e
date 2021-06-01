package examplegame.bedwars;

import lombok.Data;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Team {

    private final DyeColor color;
    private final String name;
    private final List<? extends Location> respawns;
    private boolean bedAlive = true;
    private final List<UUID> assignedPlayers;
    private final List<Player> players = new ArrayList<>();

}
