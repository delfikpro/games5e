package implario.games5e.packets;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
public class PacketQueueEnter {

    private final UUID queueId;
    private final List<UUID> party;
    private final boolean allowSplit;
    private final boolean allowExtra;
    private final Map<String, List<String>> bannedOptions;

}
