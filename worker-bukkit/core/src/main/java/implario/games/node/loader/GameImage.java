package implario.games.node.loader;

import lombok.Data;

import java.io.File;

@Data
public class GameImage {

    private final ClassLoader classLoader;
    private final File file;
    private final GameProvider gameProvider;

}
