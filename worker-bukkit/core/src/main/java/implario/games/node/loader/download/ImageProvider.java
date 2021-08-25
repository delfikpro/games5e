package implario.games.node.loader.download;


import java.io.File;

/**
 *
 */
public interface ImageProvider {

    File provideImage(String imageId) throws Exception;

}
