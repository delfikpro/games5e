package implario.games5e.packets;

import lombok.Data;

import java.util.UUID;

@Data
public class PacketRequestPlayerInfo {

    private final UUID playerId;

}
