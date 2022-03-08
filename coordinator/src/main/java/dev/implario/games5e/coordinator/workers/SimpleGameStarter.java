package dev.implario.games5e.coordinator.workers;

import com.google.inject.Inject;
import dev.implario.games5e.GameInfo;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SimpleGameStarter implements GameStarter {

    private final Logger logger = Logger.getLogger("GameStarter");
    private final Balancer balancer;

    @Override
    public CompletableFuture<RunningGame> startGame(GameInfo gameInfo) {

        logger.info("Creating game " + gameInfo);

        CompletableFuture<GameNode> nodeFuture = balancer.getSufficientNode(gameInfo.getImageId());

        CompletableFuture<RunningGame> runningGameCompletableFuture = new CompletableFuture<>();

        nodeFuture.whenComplete((node, __) -> {
            // ToDo: more logs
            if (node == null) {
                CompletableFuture<RunningGame> future = new CompletableFuture<>();
                future.completeExceptionally(new IllegalStateException("Unable to find a sufficient node for your game"));
                try {
                    runningGameCompletableFuture.complete(future.get());
                } catch (InterruptedException | ExecutionException e) {
                    e.printStackTrace();
                }

                return;
            }

            try {
                runningGameCompletableFuture.complete(node.startGame(gameInfo).get());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        });

        return runningGameCompletableFuture;
    }

}
