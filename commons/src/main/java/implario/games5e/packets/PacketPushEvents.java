package implario.games5e.packets;

import implario.games5e.GameHistoryEvent;
import lombok.Data;

import java.util.List;

@Data
public class PacketPushEvents {

    private final List<GameHistoryEvent> events;

}
