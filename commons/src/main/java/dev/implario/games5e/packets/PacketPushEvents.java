package dev.implario.games5e.packets;

import dev.implario.games5e.GameHistoryEvent;
import lombok.Data;

import java.util.List;

@Data
public class PacketPushEvents {

    private final List<GameHistoryEvent> events;

}
