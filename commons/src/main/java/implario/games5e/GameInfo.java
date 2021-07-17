package implario.games5e;

import com.google.gson.JsonElement;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class GameInfo {

    private final UUID gameId;
    private final UUID creatorId;
    private final List<UUID> players;

    private final String imageId;
    private final long createdAt;

    private final JsonElement settings;

}
