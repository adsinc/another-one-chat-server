package chat.client.commands;

import chat.common.data.CommandData;

/**
 *
 */
public class LogInCommandType implements CommandType {
    @Override
    public String getName() {
        return LOG_IN;
    }

    @Override
    public CommandData createCommandData(String login) {
        CommandData command = new CommandData();
        command.commandName = LOG_IN;
        command.sender = login;
        return command;
    }
}
