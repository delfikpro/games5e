package dev.implario.games5e.coordinator.queue;

import java.util.Collection;
import java.util.UUID;

public interface QueueManager {

    Queue getQueue(UUID queueId);

    QueueStrategy getStrategy(String name);

    void addQueue(Queue queue);

    void removeQueue(UUID queueId);

    Collection<Queue> getQueues();

}
