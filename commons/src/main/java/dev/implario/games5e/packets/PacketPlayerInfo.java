package dev.implario.games5e.packets;

import lombok.Data;

import java.util.UUID;

@Data
public class PacketPlayerInfo {

    private final UUID playerId;
    private final UUID gameId;

}
