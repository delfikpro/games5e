package implario.games5e.coordinator.workers;

import com.google.inject.Inject;
import implario.games5e.GameInfo;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class SimpleGameStarter implements GameStarter {

    private final Logger logger = Logger.getLogger("GameStarter");
    private final Balancer balancer;

    @Override
    public CompletableFuture<RunningGame> startGame(GameInfo gameInfo) {

        logger.info("Creating game " + gameInfo);

        GameNode node = balancer.getSufficientNode(gameInfo.getImageId());

        // ToDo: more logs
        if (node == null) {
            CompletableFuture<RunningGame> future = new CompletableFuture<>();
            future.completeExceptionally(new IllegalStateException("Unable to find a sufficient node for your game"));
            return future;
        }

        // ToDo: maybe check if game with that id already exists
        return node.startGame(gameInfo);
    }

}
