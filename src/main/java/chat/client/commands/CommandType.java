package chat.client.commands;

import chat.client.ClientException;
import chat.common.data.CommandData;

/**
 * Base interface for command types.
 */
public interface CommandType {
    String SEND_TO_ALL = "sendToAll";
    String SEND_TO_USER = "sendToUser";
    String GET_SERVER_TIME = "getServerTime";
    String LOG_IN = "logIn";

    String getName();

    /**
     * Creates CommandData object for appropriate CommandType.
     *
     * @param senderLogin login of the message sender
     * @param message     message text
     * @return CommandData object
     */
    CommandData createCommandData(String senderLogin, String message) throws ClientException;
}
