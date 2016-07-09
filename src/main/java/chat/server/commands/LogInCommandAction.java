package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;
import chat.server.ChatServer;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;

import static chat.common.data.ServerReply.createReplyFailed;
import static chat.common.data.ServerReply.createReplyOk;

/**
 *
 */
public class LogInCommandAction implements CommandAction {
    @Override
    public ServerReply execute(CommandData cmd, ChatServer.Attachment attachment,
                               Map<String, AsynchronousSocketChannel> clients) {
        if (attachment.loggedId) {
            return createReplyFailed("Client already logged");
        }
        attachment.loggedId = true;
        clients.put(cmd.sender, attachment.client);
        return createReplyOk("Logged as: " + cmd.sender);
    }
}
