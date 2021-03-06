package dev.implario.games5e.coordinator.queue;

import dev.implario.games5e.QueueProperties;
import dev.implario.games5e.coordinator.workers.RunningGame;
import dev.implario.games5e.coordinator.SimpleScheduler;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public class QueueTest {


    public static void main(String[] args) {

        SimpleQueueManager manager = new SimpleQueueManager(new SimpleScheduler(), gameInfo -> {
            System.out.println("Starting game: " + gameInfo);
            return CompletableFuture.completedFuture(new RunningGame(null, null));
        });
        UUID queueId = UUID.randomUUID();
        Queue queue = new Queue(
                new QueueProperties(
                        queueId,
                        "squidgame",
                        new QueueProperties.MapDefinition(null, new QueueProperties.Bounds(60, 100), new QueueProperties.Bounds(1, 1),
                                new HashMap<>()),
                        Collections.emptyList(),
                        new HashMap<>(),
                        "lax"
                ),
                new SimpleLaxQueueStrategy()
        );

        Scanner scanner = new Scanner(System.in);

        QueueStrategy strategy = new SimpleLaxQueueStrategy();

        System.out.println("a");
        long l = System.currentTimeMillis();

        String s = "";

        for (int i = 0; i < 300000; i++) {
            if (i % 363454 == 10543) s = "" + i;
        }

        System.out.println("b " + (System.currentTimeMillis() - l) + " " + s);
        while (scanner.hasNext()) {
            String next = scanner.next();
            String[] ss = next.split("\\*");
//            int times = ss[0];
            Integer.parseInt(next);
            for (int a = 0; a < 1; a++) {


                List<UUID> uuids = new ArrayList<>();
//            int v = ((int) (Math.random() * 8)) + 1;
                int v = ((int) (Math.random() * 16)) + 8;

                for (int i = 0; i < v; i++) {
                    uuids.add(UUID.randomUUID());
                }

                queue.addParty(new Party(
                        uuids,
                        new HashMap<>(),
                        true,
                        true
                ));

                System.out.println("Added party of size " + uuids.size());

                long start = System.currentTimeMillis();
                for (Queue.Emission emission : strategy.tryEmitMax(queue)) {
                    System.out.println("Creating game: " + emission.getTeams().stream().mapToInt(List::size).sum());
                }
                System.out.println("Took " + (System.currentTimeMillis() - start) + " ms, queue size after walk: " + queue.getParties().size() + " (" + queue.getParties().stream().flatMap(Party::stream).count() + ")");
            }
        }

    }

//    public static void main1(String[] args) {
//
//        SimpleQueueManager manager = new SimpleQueueManager(new SimpleScheduler(), gameInfo -> {
//            System.out.println("Starting game: " + gameInfo);
//            return CompletableFuture.completedFuture(new RunningGame(null, null));
//        });
//        UUID queueId = UUID.randomUUID();
//        Queue queue = manager.createQueue(queueId);
//        queue.setProperties(new QueueProperties(
//                queueId,
//                "bedwars",
//                new MapDefinition(null, new Bounds(1, 100), new Bounds(2, 100)),
//                Arrays.asList(
//                        new MapDefinition("pizdecio", new Bounds(1, 4), new Bounds(2, 4)),
//                        new MapDefinition("blyatio", new Bounds(1, 4), new Bounds(2, 8)),
//                        new MapDefinition("mudakio", new Bounds(4, 6), new Bounds(2, 2)),
//                        new MapDefinition("sukio", new Bounds(20, 20), new Bounds(2, 2))
//                ),
//                new HashMap<String, List<String>>() {{
//                    put("map", Arrays.asList("pizdecio", "blyatio", "mudakio", "sukio"));
//                }}
//        ));
//
//        Scanner scanner = new Scanner(System.in);
//
//        QueueStrategy strategy = new PerfectQueueStrategy();
//
//        while (scanner.hasNext()) {
//            scanner.next();
//            for (int a = 0; a < ((int) (Math.random() * 8)) + 1; a++) {
//
//
//                List<UUID> uuids = new ArrayList<>();
////            int v = ((int) (Math.random() * 8)) + 1;
//                int v = 1;
//
//                for (int i = 0; i < v; i++) {
//                    uuids.add(UUID.randomUUID());
//                }
//
//                queue.addParty(new Party(
//                        uuids,
//                        new HashMap<>(),
//                        true,
//                        true
//                ));
//
//                for (Queue.Emission emission : strategy.tryEmitMax(queue)) {
//                    System.out.println("Creating game: " + emission);
//                }
//                System.out.println("Queue size after walk: " + queue.getParties().size() + " (" + queue.getParties().stream().flatMap(Party::stream).count() + ")");
//            }
//        }
//
//    }

}
