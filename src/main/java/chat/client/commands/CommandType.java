package chat.client.commands;

import chat.client.ClientException;
import chat.common.data.CommandData;

/**
 *
 */
public interface CommandType {
    String SEND_TO_ALL = "sendToAll";
    String SEND_TO_USER = "sendToUser";
    String GET_SERVER_TIME = "getServerTime";
    String LOG_IN = "logIn";

    String getName();

    CommandData createCommandData(String message) throws ClientException;
}
