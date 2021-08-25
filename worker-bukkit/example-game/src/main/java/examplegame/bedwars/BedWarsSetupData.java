package examplegame.bedwars;

import lombok.Data;
import lombok.val;
import org.bukkit.DyeColor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class BedWarsSetupData {

    private final Map<DyeColor, TeamData> teams;

    @Data
    public static class TeamData {

        private final List<UUID> assignedPlayers;
        private final DyeColor color;
        private boolean bedAlive;

        private transient final UUID teamId = UUID.randomUUID();

    }

    public TeamData getTeamByPlayer(UUID playerId) {

        for (val entry : teams.entrySet()) {
            if (entry.getValue().getAssignedPlayers().contains(playerId)) return entry.getValue();
        }
        return null;

    }

}
