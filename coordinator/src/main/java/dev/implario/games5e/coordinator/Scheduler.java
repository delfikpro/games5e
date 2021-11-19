package dev.implario.games5e.coordinator;

import java.util.concurrent.Callable;

public interface Scheduler {

    static Callable<?> toCallable(Runnable runnable) {
        return () -> {
            runnable.run();
            return null;
        };
    }

    void run(Callable<?> callable);

    default void run(Runnable runnable) {
        run(toCallable(runnable));
    }

    void repeatEvery(long millis, Callable<?> callable);

    default void repeatEvery(long millis, Runnable runnable) {
        repeatEvery(millis, toCallable(runnable));
    }

    void runAfter(long millis, Callable<?> callable);

    default void runAfter(long millis, Runnable runnable) {
        runAfter(millis, toCallable(runnable));
    }

}
