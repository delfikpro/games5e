package implario.games5e.coordinator;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Room {

    private UUID roomId;
    private UUID owner;
    private final List<UUID> players;
    private final List<List<UUID>> teams;

}
