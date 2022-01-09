package dev.implario.games5e.coordinator.queue;

import dev.implario.games5e.QueueProperties;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SimpleLaxQueueStrategy implements QueueStrategy {

    @Override
    public List<Queue.Emission> tryEmitMax(Queue queue) {

        QueueProperties.MapDefinition definition = queue.getProperties().getGlobalMapDefinition();

        int minPlayers = definition.getAmount().getMax() * definition.getSize().getMin();
        int maxPlayers = definition.getAmount().getMax() * definition.getSize().getMax();

        val countMap = queue.getParties().stream().collect(Collectors.groupingBy(Party::size));

        int[] sizes = countMap.keySet().stream().sorted().mapToInt(Integer::intValue).toArray();
        int[] partiesLeft = new int[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            partiesLeft[i] = countMap.get(sizes[i]).size();
        }

        List<Queue.Emission> emissions = new ArrayList<>();

        while (true) {
            int[] currentMap = new int[sizes.length];
            if (!search(maxPlayers, minPlayers, 0, sizes, partiesLeft, currentMap, 0)) {
                break;
            }

            List<Party> parties = new ArrayList<>();

            System.out.println(Arrays.toString(currentMap));
            for (int i = 0; i < currentMap.length; i++) {
                int amount = currentMap[i];
                if (amount == 0) continue;
                Iterator<Party> iterator = countMap.get(sizes[i]).iterator();
                for (int j = 0; j < amount; j++) {
                    parties.add(iterator.next());
                }

            }
            queue.getParties().removeAll(parties);
            emissions.add(new Queue.Emission(new HashMap<>(), parties.stream().map(Party::getList).collect(Collectors.toList())));

        }

        return emissions;
    }

    public static boolean search(int maxPlayers, int minPlayers,
                                 int index, int[] sizeMap, int[] partiesLeftMap,
                                 int[] currentMap, int currentTotal) {

        if (index >= sizeMap.length) return false;

        int playersLeft = currentTotal;
        for (int i = index; i < sizeMap.length; i++) {
            playersLeft += sizeMap[i] * partiesLeftMap[i];
            if (playersLeft >= maxPlayers) break;
        }
        if (playersLeft < minPlayers) return false;


        for (int i = partiesLeftMap[index]; i >= 0; i--) {
            int sizeGuess = i * sizeMap[index];

            partiesLeftMap[index] -= i;
            currentMap[index] += i;
            currentTotal += sizeGuess;
            if (isValidForGame(currentTotal, maxPlayers, minPlayers)) {
                System.out.println("Valid for game: " + currentTotal + " " + sizeGuess + " " + maxPlayers + " " + minPlayers);
                return true;
            }

            if (search(maxPlayers, minPlayers, index + 1, sizeMap, partiesLeftMap, currentMap, currentTotal)) {
                return true;
            }

            currentTotal -= sizeGuess;
            currentMap[index] -= i;
            partiesLeftMap[index] += i;
        }

        return false;
    }

    public static boolean isValidForGame(int playerCount, int maxPlayers, int minPlayers) {
        return playerCount >= minPlayers && playerCount <= maxPlayers;
    }

    @Override
    public List<Queue.Emission> forceEmit(Queue queue) {
        return null;
    }

}
