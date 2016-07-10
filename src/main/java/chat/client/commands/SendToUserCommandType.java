package chat.client.commands;

import chat.common.data.CommandData;

/**
 *
 */
public class SendToUserCommandType implements CommandType {
    @Override
    public String getName() {
        return SEND_TO_USER;
    }

    @Override
    public CommandData createCommandData(String message) {
        CommandData command = new CommandData();
        command.commandName = SEND_TO_USER;
        command.sender = "alex";
        command.receiver = "roman";
        command.message = message;
        return command;
    }
}
