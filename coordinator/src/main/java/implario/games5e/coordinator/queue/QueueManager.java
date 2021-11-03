package implario.games5e.coordinator.queue;

import java.util.UUID;

public interface QueueManager {

    Queue getQueue(UUID queueId);

    Queue createQueue(UUID queueId);

    void deleteQueue(UUID queueId);

}
