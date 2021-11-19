package dev.implario.games5e;

import com.google.gson.JsonElement;
import lombok.Data;

import java.util.UUID;

@Data
public class GameHistoryEvent {

    private final long timestamp;
    private final UUID gameId;
    private final String eventTag;
    private final JsonElement eventData;

}
