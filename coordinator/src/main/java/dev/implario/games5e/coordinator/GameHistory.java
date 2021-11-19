package dev.implario.games5e.coordinator;

import dev.implario.games5e.GameHistoryEvent;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface GameHistory {

    CompletableFuture<List<GameHistoryEvent>> getEvents(UUID gameId);

}
