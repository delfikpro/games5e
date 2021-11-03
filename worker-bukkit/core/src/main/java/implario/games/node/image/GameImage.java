package implario.games.node.image;

import implario.games.node.GameCreator;
import lombok.Data;

import java.io.File;

@Data
public class GameImage {

    private final ClassLoader classLoader;
    private final File file;
    private final GameCreator gameCreator;

}
