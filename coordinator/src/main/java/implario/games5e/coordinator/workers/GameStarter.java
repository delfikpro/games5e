package implario.games5e.coordinator.workers;

import implario.games5e.GameInfo;

import java.util.concurrent.CompletableFuture;

public interface GameStarter {

    CompletableFuture<RunningGame> startGame(GameInfo gameInfo);

}
