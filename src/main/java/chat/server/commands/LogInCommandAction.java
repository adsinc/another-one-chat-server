package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;
import chat.server.ChatServer;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.function.BiFunction;

import static chat.common.data.ServerReply.createReplyFailed;
import static chat.common.data.ServerReply.createReplyOk;

/**
 *
 */
public class LogInCommandAction implements CommandAction {
    @Override
    public void execute(CommandData cmd, ChatServer.Attachment attachment,
                        Map<String, AsynchronousSocketChannel> clients,
                        BiFunction<ServerReply, AsynchronousSocketChannel, Void> sendAnswerFn) {
        if (attachment.loggedId) {
            createReplyFailed("ChatClient already logged");
        } else {
            attachment.loggedId = true;
            clients.put(cmd.sender, attachment.client);
            sendAnswerFn.apply(createReplyOk("Logged as: " + cmd.sender), attachment.client);
        }
    }
}
