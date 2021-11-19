package dev.implario.games5e.node.image;

public class BadImageException extends RuntimeException {

    public BadImageException(String message) {
        super(message);
    }

    public BadImageException(String message, Throwable cause) {
        super(message, cause);
    }
}
