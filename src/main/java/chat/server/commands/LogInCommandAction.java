package chat.server.commands;

import chat.common.data.CommandData;
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
                        Map<String, ChatServer.Attachment> clients,
                        BiFunction<Object, AsynchronousSocketChannel, Void> sendAnswerFn) {
        if (attachment.loggedId) {
            createReplyFailed("ChatClient already logged");
        } else {
            attachment.loggedId = true;
            clients.put(cmd.sender, attachment);
            sendAnswerFn.apply(createReplyOk("Logged as: " + cmd.sender), attachment.client);
        }
    }
}
