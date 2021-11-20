package dev.implario.games5e.packets;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class PacketQueueLeave {

    private final List<UUID> players;

}
