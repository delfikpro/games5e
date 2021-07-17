package implario.games5e.packets;

import implario.games5e.ImageType;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PacketNodeHandshakeV1 {

    private final String authToken;
    private final ImageType nodeType;

    /**
     * In case of a reconnect, the coordinator needs to know about
     * games that run on this node
     */
    private final List<UUID> activeGames;

}
