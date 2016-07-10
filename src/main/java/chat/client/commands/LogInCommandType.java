package chat.client.commands;

import chat.client.ClientException;
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
    public CommandData createCommandData(String login) throws ClientException {
        if (login.isEmpty())
            throw new ClientException("Login is empty");
        CommandData command = new CommandData();
        command.commandName = LOG_IN;
        command.sender = login;
        return command;
    }
}
