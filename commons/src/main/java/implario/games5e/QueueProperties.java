package implario.games5e;

import lombok.Data;

import java.util.*;

@Data
public class QueueProperties {

    private static final String MAP_PREFERENCE = "map";

    private final UUID queueId;
    private final String imageId;
    private final TeamsBounds globalTeamsBounds;
    private final List<TeamsBounds> mapTeamsBounds;
    private final Map<String, List<String>> possiblePreferences;

    public List<TeamsBounds> getRandomizedBounds() {
        if (mapTeamsBounds == null || mapTeamsBounds.isEmpty()) return Collections.singletonList(globalTeamsBounds);
        List<TeamsBounds> copy = new ArrayList<>(mapTeamsBounds);
        Collections.shuffle(copy);
        return copy;
    }

    @Data
    public static class TeamsBounds {
        private final Bounds size;
        private final Bounds amount;
        private final String map;
    }

    @Data
    public static class Bounds {
        private final int min;
        private final int max;
    }

}
