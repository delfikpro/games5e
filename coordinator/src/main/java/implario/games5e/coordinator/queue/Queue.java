package implario.games5e.coordinator.queue;

import implario.games5e.QueueProperties;
import lombok.*;

import java.util.*;
import java.util.stream.Collectors;

@Data
public class Queue {

    private QueueProperties properties;
    private final List<Party> parties = new LinkedList<>();

    public void addParty(Party party) {
        this.parties.add(party);
    }

    public List<Emission> recommendedWalk() {

        List<Emission> emissions = new ArrayList<>();
        // ToDo: Merge maps with equal bounds
        // ToDo: Swap the loops for more randomized maps
        for (QueueProperties.TeamsBounds bounds : properties.getRandomizedBounds()) {
            while (true) {
                Emission pass = pass(bounds.getAmount().getMax(), bounds.getSize().getMax(), bounds.getMap());
                if (pass == null) break;
                emissions.add(pass);
            }
        }
        return emissions;

    }

    public void clear() {
        parties.clear();
    }

    private static Map<String, List<String>> clonePreferenceMap(Map<String, List<String>> map) {
        return map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> new ArrayList<>(e.getValue())));
    }

    public Emission pass(int teamsAmount, int playersPerTeam, String map) {

//        if (parties.size() < teamsAmount * playersPerTeam) return null;

        for (Party head : parties) {

            List<Party> variants = parties.stream()
//                    .filter(party -> matcher.test(party, head))
                    .filter(party -> party != head)
                    .collect(Collectors.toList());

            Map<String, List<String>> prefs = clonePreferenceMap(properties.getPossiblePreferences());
            if (map == null) prefs.remove("map");
            else prefs.put("map", new ArrayList<>(Collections.singletonList(map)));

            MatchMaker<UUID, Party> maker = new MatchMaker<>(variants, playersPerTeam, teamsAmount, prefs);

            if (!maker.search()) continue;

            Map<Party, Iterator<UUID>> iterators = variants.stream().collect(Collectors.toMap(party -> party, Collection::iterator));

            List<List<UUID>> teams = new ArrayList<>();
            for (Party[] partyMap : maker.partyMaps) {
                List<UUID> team = new ArrayList<>();
                for (Party party : partyMap) {
                    team.add(iterators.get(party).next());
                    parties.remove(party);
//                    playerPartyMap.keySet().removeAll(party);
                }
                teams.add(team);
            }

            Map<String, String> preferences = new HashMap<>();
            maker.preferences.forEach((key, values) -> {
                preferences.put(key, values.get((int) (Math.random() * values.size())));
            });

            preferences.put("map", map);

            return new Emission(preferences, teams);
        }

        return null;

    }

    @Data
    public static class Emission {
        private final Map<String, String> preferences;
        private final List<List<UUID>> teams;
    }

    private static class MatchMaker<U, P extends Party> {

        private final Collection<P> consideredParties = new ArrayList<>();
        private final List<P> variants;
        private P[][] partyMaps;
        private final int playersPerTeam;
        private Map<String, List<String>> preferences;

        @SuppressWarnings("unchecked")
        public MatchMaker(List<P> variants, int playersPerTeam, int numberOfTeams, Map<String, List<String>> preferences) {
            this.variants = variants;
            this.playersPerTeam = playersPerTeam;
            this.partyMaps = (P[][]) new Party[numberOfTeams][playersPerTeam];
            this.preferences = clonePreferenceMap(preferences);
        }

        boolean search() {
            return search(0);
        }

        boolean search(int deep) {

            int totalFreeSlots = 0;
            for (P[] partyMap : partyMaps) {
                for (P party : partyMap) {
                    if (party == null) totalFreeSlots++;
                }
            }
            if (totalFreeSlots == 0) return true;

            for (P party : variants) {
                if (party.isEmpty() || consideredParties.contains(party)) continue;

                // If this party is too large then discard it.
                if (party.size() > totalFreeSlots) continue;

                val prevPreferences = clonePreferenceMap(this.preferences);

                for (val entry : party.getBannedOptions().entrySet()) {
                    List<String> options = preferences.get(entry.getKey());
                    if (options == null) continue;
                    options.removeAll(entry.getValue());
                    // Total preference mismatch
                    if (options.isEmpty()) {
                        preferences = prevPreferences;
                        return false;
                    }
                }

                P[][] prevPartyMaps = Queue.clone(partyMaps);

                if (tryFitParty(party)) {
                    consideredParties.add(party);
                    if (search(deep + 1)) return true;
                    consideredParties.remove(party);
                }

                partyMaps = prevPartyMaps;
                preferences = prevPreferences;

            }

            return false;
        }

        boolean tryFitParty(P party) {

            int size = party.size();

            while (size > 0) {

                // Limiting allocation to maximum players amount in one team
                int allocationSize = Math.min(size, playersPerTeam);

                // Trying to fit players in a single team
                if (!tryFitGroup(allocationSize, party))
                    // Failed, aborting
                    return false;

                size -= allocationSize;

            }

            return true;
        }

        /**
         * Fits a group of players into one team
         */
        boolean tryFitGroup(int groupSize, P fromParty) {

            int bestTeam = -1;
            int bestTeamFreeSlots = Integer.MAX_VALUE;

            for (int team = 0; team < partyMaps.length; team++) {

                int freeSlots = 0;

                for (P party : partyMaps[team])
                    if (party == null) freeSlots++;

                if (freeSlots >= groupSize && freeSlots < bestTeamFreeSlots) {
                    bestTeamFreeSlots = freeSlots;
                    bestTeam = team;
                }
            }

            if (bestTeam == -1) return false;

            P[] partyMap = partyMaps[bestTeam];

            for (int i = 0; i < partyMap.length && groupSize != 0; i++) {
                if (partyMap[i] != null) continue;
                partyMap[i] = fromParty;
                groupSize--;
            }

            return true;

        }

    }

    @SuppressWarnings("unchecked")
    private static <T> T[][] clone(T[][] array) {
        if (array == null) return null;

        T[][] result = (T[][]) new Collection[array.length][];
        for (int i = 0; i < array.length; i++) {
            result[i] = Arrays.copyOf(array[i], array[i].length);
        }
        return result;
    }

}
