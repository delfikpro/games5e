package dev.implario.games5e.packets;

import lombok.Data;

import java.util.UUID;

@Data
public class PacketQueueRemove {

    private final UUID queueId;

}
