package implario.games.node;

import implario.games.node.linker.SessionBukkitLinker;
import implario.games.node.image.GameImage;
import implario.games.node.image.ImageLoader;
import implario.games.node.image.download.MavenImageProvider;

import java.io.File;

public class GameFramework {

    public static GameNode createDefaultNode() {
        GameNode node = new GameNode();
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
