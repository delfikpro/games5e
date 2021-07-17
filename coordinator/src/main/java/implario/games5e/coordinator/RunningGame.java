package implario.games5e.coordinator;

import implario.games5e.GameInfo;
import implario.games5e.Games5eGameState;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class RunningGame {

    private final GameInfo info;
    private final Games5eGameState state;

}
