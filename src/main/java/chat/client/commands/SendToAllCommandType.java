package chat.client.commands;

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
    public CommandData createCommandData(String message) {
        CommandData command = new CommandData();
        command.commandName = SEND_TO_ALL;
        command.sender = "alex";
        command.message = message;
        return command;
    }
}
