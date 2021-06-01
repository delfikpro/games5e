package implario.games5e.speech;

import lombok.Data;

@Data
public class PacketNodeInfo {

    private final String ip;
    // ToDo: maybe return active games in case of a reconnect?

}
