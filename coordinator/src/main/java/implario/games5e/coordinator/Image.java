package implario.games5e.coordinator;

import implario.games5e.NodeType;
import lombok.Data;

@Data
public class Image {

    private final String id;
    private final NodeType type;
    private final String addedBy;
    private long lastUpdate;

}
