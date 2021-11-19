package dev.implario.games5e.packets;

import lombok.Data;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
public class PacketSubscribedQueues {

    private final Set<UUID> queues;

}
