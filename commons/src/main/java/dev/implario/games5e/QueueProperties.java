package dev.implario.games5e;

import com.google.gson.JsonElement;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.*;

/**
 *
 */
@Data
public class QueueProperties {

    /**
     * Each queue has its own id, which is used by services to interact with it
     */
    private final UUID queueId;

    /**
     * Emissions from each queue are tagged with a certain image id
     * Queue is bound to a single image id by design,
     * so one should use preferences for queue division if required
     */
    private final String imageId;

    /**
     * Each queue has a global map definition to quickly reject parties that are too small / too large
     * The value of `map` in those bounds is always `null`
     *
     * @apiNote if this queue has no other map definitions then the global one will be used
     */
    private final MapDefinition globalMapDefinition;

    /**
     * If the game has maps with different supported player/team amounts,
     * the queue strategy will try to fit players into each variant
     */
    private final List<MapDefinition> mapDefinitions;

    /**
     * Every queue shall predefine all the possible preferences for users to pick.
     * The implementation will compose emissions by filtering this map with users' bans
     */
    private final Map<String, List<String>> possiblePreferences;

    /**
     * Preference key that points to map definition bans
     */
    public static final String MAP_PREFERENCE = "map";

    /**
     * Strategy that is used to emit from this queue
     */
    private final String strategy;

    /**
     * Buffer for custom data such as readable names, queue groups, etc.
     */
    private final Map<String, String> tags = new HashMap<>();

//    @ToString.Exclude
//    @EqualsAndHashCode.Exclude
//    private final transient Map<MapDefinition, List<MapDefinition>> aggregatedDefinitions = new HashMap<>();
//
//    public Map<MapDefinition, List<MapDefinition>> getAggregatedDefinitions() {
//        if (aggregatedDefinitions.isEmpty()) {
//            if (mapDefinitions == null || mapDefinitions.isEmpty()) {
//                aggregatedDefinitions.put(globalMapDefinition, Collections.singletonList(globalMapDefinition));
//            } else {
//                for (MapDefinition definition : mapDefinitions) {
//                    MapDefinition key = new MapDefinition(null, definition.getSize(), definition.getAmount());
//                    aggregatedDefinitions.computeIfAbsent(key, k -> new ArrayList<>()).add(definition);
//                }
//            }
//        }
//
//        return aggregatedDefinitions;
//    }

    /**
     * Bounds are inclusive from both ends
     */
    @Data
    public static class Bounds {

        private final int min;

        /**
         * Max value is also referred to as the <b>recommended</b> value
         */
        private final int max;
    }

    /**
     * MapDefinitions are basically format ranges in whom a certain queue can produce emissions.
     * For example, if a certain bedwars map has 4 islands with a size to fit about 4 teams,
     * then the map definition might want to accept at most 4x4 (4 teams with 4 players each),
     * and at least 1x2 (2 teams with a single player each) - the smallest reasonable number of players
     * to have in a bedwars game.
     *
     * @apiNote maximum bounds are also considered as recommended.
     */
    @Data
    public static class MapDefinition {
        private final String map;
        private final Bounds size;
        private final Bounds amount;
    }

}
