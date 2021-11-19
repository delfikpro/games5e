package dev.implario.games5e.coordinator.workers;

import dev.implario.games5e.GameInfo;
import dev.implario.games5e.Games5eGameState;
import lombok.Data;

import java.util.Map;

@Data
public class RunningGame {

    private final GameInfo info;
    private Games5eGameState state = Games5eGameState.INITIALIZING;
    private final Map<String, String> meta;

}
