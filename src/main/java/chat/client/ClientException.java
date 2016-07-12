package chat.client;

/**
 * Represents chat client application specific exceptions.
 */
public class ClientException extends Exception {
    public ClientException(String msg) {
        super(msg);
    }
}
