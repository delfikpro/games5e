package dev.implario.games5e.packets;

import dev.implario.games5e.QueueProperties;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PacketQueueState {

    private final QueueProperties properties;
    private final List<List<UUID>> parties;

}
