package implario.games5e.packets;

import implario.games5e.QueueProperties;
import lombok.Data;

import java.util.UUID;

@Data
public class PacketQueueSetup {

    private final UUID queueId;
    private final QueueProperties properties;

}
