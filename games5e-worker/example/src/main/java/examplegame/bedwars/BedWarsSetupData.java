package examplegame.bedwars;

import lombok.Data;
import org.bukkit.DyeColor;

import java.util.List;
import java.util.UUID;

@Data
public class BedWarsSetupData {

    private final List<TeamData> teams;

    @Data
    public static class TeamData {

        private final List<UUID> assignedPlayers;
        private final DyeColor color;
        private final boolean bedAlive;

    }

}
