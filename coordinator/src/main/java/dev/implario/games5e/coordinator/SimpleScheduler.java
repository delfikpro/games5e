package dev.implario.games5e.coordinator;

import java.util.concurrent.*;

public class SimpleScheduler implements Scheduler {

    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void run(Callable<?> callable) {
        executor.submit(callable);
    }

    @Override
    public void repeatEvery(long millis, Callable<?> callable) {
        executor.scheduleWithFixedDelay(() -> {
            try {
                callable.call();
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }, millis, millis, TimeUnit.MILLISECONDS);
    }

    @Override
    public void runAfter(long millis, Callable<?> callable) {
        executor.schedule(callable, millis, TimeUnit.MILLISECONDS);
    }

}
