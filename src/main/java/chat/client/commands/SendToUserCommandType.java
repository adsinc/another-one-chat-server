package chat.client.commands;

import chat.client.ClientException;
import chat.common.data.CommandData;

import static chat.client.commands.CommandDataManager.CMD_DELIMITER;

/**
 * Send message to user command
 */
public class SendToUserCommandType implements CommandType {
    @Override
    public String getName() {
        return SEND_TO_USER;
    }

    @Override
    public CommandData createCommandData(String senderLogin, String message) throws ClientException {
        String[] parts = message.split(CMD_DELIMITER);
        if (!message.contains(CMD_DELIMITER) || parts.length < 2 || parts[0].isEmpty())
            throw new ClientException("Can not find receiver");

        CommandData command = new CommandData();
        command.commandName = SEND_TO_USER;
        command.sender = senderLogin;
        command.receiver = parts[0];
        command.message = message;
        return command;
    }
}
