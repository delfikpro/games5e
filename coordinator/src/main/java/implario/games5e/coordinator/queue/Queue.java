package implario.games5e.coordinator.queue;

import implario.games5e.QueueProperties;
import lombok.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

@Data
public class Queue {

    private QueueProperties properties;
    private final List<Party> parties = new LinkedList<>();

    public void addParty(Party party) {
        this.parties.add(party);
    }

//    public List<Emission> recommendedWalk() {
//
//        List<Emission> emissions = new ArrayList<>();
//        // ToDo: Merge maps with equal bounds
//        // ToDo: Swap the loops for more randomized maps
//        for (QueueProperties.MapDefinition bounds : properties.getShuffledDefinitions()) {
//            while (true) {
//                Emission pass = pass(bounds.getAmount().getMax(), bounds.getSize().getMax(), bounds.getMap());
//                if (pass == null) break;
//                emissions.add(pass);
//            }
//        }
//        return emissions;
//
//    }

    public void clear() {
        parties.clear();
    }

    @Data
    public static class Emission {
        private final Map<String, String> preferences;
        private final List<List<UUID>> teams;
    }

}
