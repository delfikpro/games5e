package implario.games5e.packets;

import com.google.gson.JsonElement;
import implario.games5e.GameInfo;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class PacketCreateGame {

    private final GameInfo gameInfo;

}
