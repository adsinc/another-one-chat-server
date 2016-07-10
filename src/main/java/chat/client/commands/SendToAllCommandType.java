package chat.client.commands;

import chat.client.ClientException;
import chat.common.data.CommandData;

/**
 *
 */
public class SendToAllCommandType implements CommandType {
    @Override
    public String getName() {
        return SEND_TO_ALL;
    }

    @Override
    public CommandData createCommandData(String message) throws ClientException {
        String msg = message.trim();
        if (message.trim().isEmpty())
            throw new ClientException("Message is empty");

        CommandData command = new CommandData();
        command.commandName = SEND_TO_ALL;
        command.sender = "alex";
        command.message = msg;
        return command;
    }
}
