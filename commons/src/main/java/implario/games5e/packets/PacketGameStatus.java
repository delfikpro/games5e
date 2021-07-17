package implario.games5e.packets;

import implario.games5e.GameInfo;
import implario.games5e.Games5eGameState;
import lombok.Data;

@Data
public class PacketGameStatus {

    private final GameInfo info;
    private final Games5eGameState state;

}
