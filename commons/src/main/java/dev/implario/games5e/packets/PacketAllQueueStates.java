package dev.implario.games5e.packets;

import lombok.Data;

import java.util.List;

@Data
public class PacketAllQueueStates {

    private final List<PacketQueueState> states;

}
