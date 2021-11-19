package dev.implario.games5e.node;

import dev.implario.games5e.node.image.ImageLoader;
import dev.implario.games5e.node.linker.SessionBukkitLinker;
import dev.implario.games5e.node.image.GameImage;
import dev.implario.games5e.node.image.download.MavenImageProvider;

import java.io.File;

public class GameFramework {

    public static DefaultGameNode createDefaultNode() {
        DefaultGameNode node = new DefaultGameNode();
        node.getSupportedImagePrefixes().add("bukkit-maven ");
        node.setGameCreator(mavenImageCreator());
        node.setLinker(SessionBukkitLinker.link(node));
        return node;
    }

    public static GameCreator mavenImageCreator() {
        MavenImageProvider imageProvider = new MavenImageProvider();
        return (gameId, imageId, settings) -> {

            // ToDo: Async image resolution & load
            File file = imageProvider.provideImage(imageId);
            if (file == null) {
                throw new IllegalArgumentException("Unable to resolve image '" + imageId + "'");
            }

            GameImage image = ImageLoader.load(file);
            return image.getGameCreator().createGame(gameId, imageId, settings);
        };
    }

}
