package dev.implario.games5e.node.image.download;


import java.io.File;

/**
 *
 */
public interface ImageProvider {

    File provideImage(String imageId) throws Exception;

}
