package implario.games5e.speech;

import com.google.gson.JsonObject;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PacketCreateGame {

    private final UUID gameId;
    private final UUID creatorId;
    private final List<UUID> players;
    private final String imageUrl;
    private final JsonObject settings;

}
