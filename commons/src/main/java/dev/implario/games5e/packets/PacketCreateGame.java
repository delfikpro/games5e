package dev.implario.games5e.packets;

import dev.implario.games5e.GameInfo;
import lombok.Data;

@Data
public class PacketCreateGame {

    private final GameInfo gameInfo;

}
