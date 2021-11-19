package dev.implario.games5e.coordinator.workers;

import dev.implario.nettier.NettierRemote;
import dev.implario.games5e.GameInfo;
import dev.implario.games5e.packets.PacketCreateGame;
import dev.implario.games5e.packets.PacketGameStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;

@Getter
@RequiredArgsConstructor
public class GameNodeImpl implements GameNode {

    private final NettierRemote remote;
    private final List<RunningGame> runningGames;
    private final Predicate<String> imageFilter;
    private final Set<UUID> queueSubscriptions;

    @Override
    public CompletableFuture<RunningGame> startGame(GameInfo info) {

        if (!imageFilter.test(info.getImageId())) {
            return null;
        }

        RunningGame runningGame = new RunningGame(info, new HashMap<>());
        runningGames.add(runningGame);
        System.out.println("Sending request to node " + remote);
        return remote.send(new PacketCreateGame(info)).awaitFuture(PacketGameStatus.class).thenApply(packet -> {
            System.out.println("Game created!");
            runningGame.getMeta().putAll(packet.getMeta());
            return runningGame;
        });
    }

    @Override
    public boolean isImageSupported(String imageId) {
        return imageFilter.test(imageId);
    }
}
