package dev.implario.games5e.packets;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PacketQueueUpdate {

    private final UUID queueId;
    private final List<UUID> left;
    private final List<List<UUID>> joined;
    private final int totalPlayers;

}
