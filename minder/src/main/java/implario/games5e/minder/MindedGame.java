package implario.games5e.minder;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class MindedGame {

    private final UUID gameId;
    private final long createdAt;
    private final List<UUID> players;

}
