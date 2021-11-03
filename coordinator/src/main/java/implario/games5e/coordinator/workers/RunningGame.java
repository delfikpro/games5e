package implario.games5e.coordinator.workers;

import implario.games5e.GameInfo;
import implario.games5e.Games5eGameState;
import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class RunningGame {

    private final GameInfo info;
    private Games5eGameState state = Games5eGameState.INITIALIZING;
    private final Map<String, String> meta;

}
