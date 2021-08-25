package implario.games5e.coordinator;

import dev.implario.nettier.NettierRemote;
import implario.games5e.GameInfo;
import implario.games5e.Games5eGameState;
import implario.games5e.packets.PacketCreateGame;
import implario.games5e.packets.PacketOk;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AbstractGameNode implements GameNode {

    private final NettierRemote remote;
    private final List<RunningGame> runningGames;
    private final Predicate<String> imageFilter;

    @Override
    public CompletableFuture<Void> startGame(GameInfo info, String imageId) {

        if (!imageFilter.test(imageId)) {
            return null;
        }

        runningGames.add(new RunningGame(info, Games5eGameState.INITIALIZING));
        System.out.println("Sending request to node " + remote);
        return remote.send(new PacketCreateGame(info)).awaitFuture(PacketOk.class).thenAccept(packet -> {
            System.out.println("Game created!");
        });
    }

    @Override
    public boolean isImageSupported(String imageId) {
        return imageFilter.test(imageId);
    }
}
