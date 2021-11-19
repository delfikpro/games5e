package dev.implario.games5e.node.image;

import dev.implario.games5e.node.GameCreator;
import lombok.Data;

import java.io.File;

@Data
public class GameImage {

    private final ClassLoader classLoader;
    private final File file;
    private final GameCreator gameCreator;

}
