package dev.implario.games5e.coordinator.queue;

import dev.implario.games5e.QueueProperties;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class SimpleLaxQueueStrategy implements QueueStrategy {

    private final double maxDeviation;


    @Override
    public List<Queue.Emission> tryEmitMax(Queue queue) {

        QueueProperties.MapDefinition definition = queue.getProperties().getGlobalMapDefinition();
        int maxPlayers = definition.getAmount().getMax() * definition.getSize().getMax();

        int maxDeviation = (int) (maxPlayers * this.maxDeviation);


        val countMap = queue.getParties().stream().collect(Collectors.groupingBy(Party::size));

        int[] sizes = countMap.keySet().stream().sorted().mapToInt(Integer::intValue).toArray();
        int[] partiesLeft = new int[sizes.length];
        for (int i = 0; i < sizes.length; i++) {
            partiesLeft[i] = countMap.get(sizes[i]).size();
        }

        List<Queue.Emission> emissions = new ArrayList<>();

        while (true) {
            int[] currentMap = new int[sizes.length];
            if (!search(maxPlayers, maxDeviation, 0, sizes, partiesLeft, currentMap, 0)) {
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

    public static boolean search(int requiredPlayers, int maxDeviation,
                                 int index, int[] sizeMap, int[] partiesLeftMap,
                                 int[] currentMap, int currentTotal) {

        if (index >= sizeMap.length) return false;

        int playersLeft = currentTotal;
        for (int i = index; i < sizeMap.length; i++) {
            playersLeft += sizeMap[i] * partiesLeftMap[i];
            if (playersLeft >= requiredPlayers - maxDeviation) break;
        }
        if (playersLeft < requiredPlayers - maxDeviation) return false;


        for (int i = partiesLeftMap[index]; i >= 0; i--) {
            int sizeGuess = i * sizeMap[index];

            partiesLeftMap[index] -= i;
            currentMap[index] += i;
            currentTotal += sizeGuess;
            if (isValidForGame(currentTotal, requiredPlayers, maxDeviation)) {
                System.out.println("Valid for game: " + currentTotal + " " + sizeGuess + " " + requiredPlayers + " " + maxDeviation);
                return true;
            }

            if (search(requiredPlayers, maxDeviation, index + 1, sizeMap, partiesLeftMap, currentMap, currentTotal)) {
                return true;
            }

            currentTotal -= sizeGuess;
            currentMap[index] -= i;
            partiesLeftMap[index] += i;
        }

        return false;
    }

    public static boolean isValidForGame(int playerCount, int requiredPlayers, int maxDeviation) {
        return playerCount >= requiredPlayers - maxDeviation && playerCount <= requiredPlayers + maxDeviation;
    }

    @Override
    public List<Queue.Emission> forceEmit(Queue queue) {
        return null;
    }

}
