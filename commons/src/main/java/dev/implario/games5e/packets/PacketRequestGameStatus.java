package dev.implario.games5e.packets;

import lombok.Data;

import java.util.UUID;

@Data
public class PacketRequestGameStatus {

    private final UUID gameId;

}
