package dev.implario.games5e.packets;

import dev.implario.games5e.GameInfo;
import dev.implario.games5e.Games5eGameState;
import lombok.Data;

import java.util.Map;

@Data
public class PacketGameStatus {

    private final GameInfo info;
    private final Map<String, String> meta;
    private final Games5eGameState state;

}
