package dev.implario.games5e.packets;

import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
public class PacketUpdateMeta {

    private final UUID gameId;
    private final Map<String, String> meta;

}
