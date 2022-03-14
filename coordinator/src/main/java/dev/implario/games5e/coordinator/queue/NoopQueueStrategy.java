package dev.implario.games5e.coordinator.queue;

import java.util.Collections;
import java.util.List;

public class NoopQueueStrategy implements QueueStrategy {
    @Override
    public List<Queue.Emission> tryEmitMax(Queue queue) {
        return Collections.emptyList();
    }

    @Override
    public List<Queue.Emission> forceEmit(Queue queue) {
        return Collections.emptyList();
    }
}
