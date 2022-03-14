package dev.implario.games5e.coordinator;

import com.google.common.collect.Iterators;
import dev.implario.games5e.packets.PacketQueueUpdate;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class DeltaUtil {

    public static List<UUID> flatten(List<List<UUID>> parties) {
        List<UUID> result = new ArrayList<>();
        for (List<UUID> party : parties) {
            result.addAll(party);
        }
        return result;
    }

    public static void main(String[] args) {

        List<List<UUID>> queue = new ArrayList<>();

        int g = 1;
        int p = 1;

        double v = Math.random() * 20 + 80;
        for (int i = 0; i < v; i++) {
            double v1 = Math.random() * 5 + 1;
            List<UUID> party = new ArrayList<>();
            for (int j = 0; j < v1; j++) {
                party.add(new UUID(p, g++));
            }
            p++;
            queue.add(party);
        }

        List<List<UUID>> clone = queue.stream().map(ArrayList::new).collect(Collectors.toList());
        List<List<UUID>> clone2 = queue.stream().map(ArrayList::new).collect(Collectors.toList());

        for (int i = 0; i < 15; i++) {
            clone.remove((int) (clone.size() * Math.random()));
        }
        for (int i = 0; i < 15; i++) {
            List<UUID> party = clone.get((int) (clone.size() * Math.random()));
            int i1 = (int) (Math.random() * (party.size() - 1));
            for (int j = 0; j < i1; j++) {
                party.remove((int) (Math.random() * party.size()));
            }
        }

        {
            double v2 = Math.random() * 20 + 80;
            for (int i = 0; i < v2; i++) {
                double v1 = Math.random() * 5 + 1;
                List<UUID> party = new ArrayList<>();
                for (int j = 0; j < v1; j++) {
                    party.add(new UUID(p, g++));
                }
                p++;
                clone.add(party);
            }
        }

        PacketQueueUpdate update = createDelta(UUID.randomUUID(), queue, clone);
        for (List<UUID> uuids : clone2) {
            uuids.removeAll(update.getLeft());
        }
        clone2.removeIf(List::isEmpty);
        clone2.addAll(update.getJoined());

        System.out.println(clone.equals(clone2));

        System.out.println(clone.stream().map(s -> s.stream().map(UUID::getLeastSignificantBits)
                .collect(Collectors.toList())).collect(Collectors.toList()));
        System.out.println(clone2.stream().map(s -> s.stream().map(UUID::getLeastSignificantBits)
                .collect(Collectors.toList())).collect(Collectors.toList()));
        System.out.println(update.getLeft().stream().map(UUID::getLeastSignificantBits).collect(Collectors.toList()));
        System.out.println(update.getJoined().stream().map(s -> s.stream().map(UUID::getLeastSignificantBits)
                .collect(Collectors.toList())).collect(Collectors.toList()));



    }

    public static PacketQueueUpdate createDelta(UUID queueId, List<List<UUID>> before, List<List<UUID>> after) {

        Iterator<List<UUID>> iterator = after.iterator();

        List<UUID> toRemove = new ArrayList<>();
        List<List<UUID>> toAdd = new ArrayList<>();

        List<UUID> currentRight = iterator.hasNext() ? iterator.next() : null;
        for (List<UUID> leftParty : before) {
            if (currentRight == null || !isSubSetOf(leftParty, currentRight)) {
                toRemove.addAll(leftParty);
                continue;
            }

            if (leftParty.size() != currentRight.size()) {
                leftParty.removeAll(currentRight);
                toRemove.addAll(leftParty);
            }
            currentRight = iterator.hasNext() ? iterator.next() : null;

        }

        if (currentRight != null) toAdd.add(currentRight);

        while (iterator.hasNext()) {
            toAdd.add(iterator.next());
        }

        return new PacketQueueUpdate(queueId, toRemove, toAdd, after.size());

    }

    public static boolean isSubSetOf(List<UUID> bigSet, List<UUID> subSet) {
        for (UUID sub : subSet) {
            if (!bigSet.contains(sub)) return false;
        }
        return true;
    }


}
