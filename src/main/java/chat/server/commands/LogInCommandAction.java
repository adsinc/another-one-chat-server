package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;
import chat.common.data.SimpleFailedReply;
import chat.common.data.SimpleOkReply;
import chat.server.ChatServer;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;

/**
 *
 */
public class LogInCommandAction implements CommandAction {
    @Override
    public ServerReply execute(CommandData cmd, ChatServer.Attachment attachment,
                               Map<String, AsynchronousSocketChannel> clients) {
        if (attachment.loggedId) {
            return new SimpleFailedReply("Client already logged");
        }
        attachment.loggedId = true;
        clients.put(cmd.sender, attachment.client);
        return new SimpleOkReply("Logged as: " + cmd.sender);
    }
}
