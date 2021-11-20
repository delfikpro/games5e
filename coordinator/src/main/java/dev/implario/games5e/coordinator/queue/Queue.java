package dev.implario.games5e.coordinator.queue;

import dev.implario.games5e.QueueProperties;
import lombok.*;

import java.util.*;

@Data
@AllArgsConstructor
public class Queue {

    private QueueProperties properties;
    private final List<Party> parties = new LinkedList<>();
    private QueueStrategy strategy;

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

    public void removePlayers(Collection<UUID> players) {
        for (Iterator<Party> iterator = parties.iterator(); iterator.hasNext(); ) {
            Party party = iterator.next();
            party.removeAll(players);
            if (party.isEmpty()) iterator.remove();
        }
    }

    @Data
    public static class Emission {
        private final Map<String, String> preferences;
        private final List<List<UUID>> teams;

        public Map<String, Object> mergePreferences() {
            Map<String, Object> map = new HashMap<>(preferences);
            map.put("teams", teams);
            return map;
        }

    }

}
