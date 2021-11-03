package implario.games.node.image;

public class BadImageException extends RuntimeException {

    public BadImageException(String message) {
        super(message);
    }

    public BadImageException(String message, Throwable cause) {
        super(message, cause);
    }
}
