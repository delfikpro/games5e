package implario.games5e.coordinator;

import implario.games5e.ImageType;
import lombok.Data;

@Data
public class Image {

    private final String id;
    private final ImageType type;
    private final String addedBy;
    private long lastUpdate;

}
