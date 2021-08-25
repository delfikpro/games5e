package examplegame.bedwars;

import lombok.Data;
import lombok.experimental.Delegate;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class Team {

    @Delegate
    private final BedWarsSetupData.TeamData teamData;
    private final String name;
    private final Location spawn;
    private final List<Player> players;

}
