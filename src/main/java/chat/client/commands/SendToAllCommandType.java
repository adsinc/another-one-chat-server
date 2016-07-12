package chat.client.commands;

import chat.client.ClientException;
import chat.common.data.CommandData;

/**
 * Send message to all connected users command
 */
public class SendToAllCommandType implements CommandType {
    @Override
    public String getName() {
        return SEND_TO_ALL;
    }

    @Override
    public CommandData createCommandData(String senderLogin, String message) throws ClientException {
        String msg = message.trim();
        if (message.trim().isEmpty())
            throw new ClientException("Message is empty");

        CommandData command = new CommandData();
        command.commandName = SEND_TO_ALL;
        command.sender = senderLogin;
        command.message = msg;
        return command;
    }
}
