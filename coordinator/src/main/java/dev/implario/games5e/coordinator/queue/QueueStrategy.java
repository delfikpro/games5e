package dev.implario.games5e.coordinator.queue;

import java.util.List;

public interface QueueStrategy {

    /**
     * Tries to produce emissions with all available map slots filled
     */
    List<Queue.Emission> tryEmitMax(Queue queue);

    /**
     * Tries to produce any emissions possible
     */
    List<Queue.Emission> forceEmit(Queue queue);

}
