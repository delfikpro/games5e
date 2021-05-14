package implario.games.node.loader;

public class BadImageException extends Exception {

    public BadImageException(String message) {
        super(message);
    }

    public BadImageException(String message, Throwable cause) {
        super(message, cause);
    }
}
