package chat.client.commands;

import chat.common.data.CommandData;

/**
 *
 */
public class GetServerTimeCommandType implements CommandType {
    @Override
    public String getName() {
        return GET_SERVER_TIME;
    }

    @Override
    public CommandData createCommandData(String message) {
        CommandData command = new CommandData();
        command.commandName = GET_SERVER_TIME;
        return command;
    }
}
