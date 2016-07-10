package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;
import chat.server.ChatServer;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.function.BiFunction;

import static chat.common.data.ServerReply.createReplyOk;

public class SendToUserCommandAction implements CommandAction {
    @Override
    public void execute(CommandData commandData,
                        ChatServer.Attachment attachment, Map<String, AsynchronousSocketChannel> clients,
                        BiFunction<ServerReply, AsynchronousSocketChannel, Void> sendAnswerFn) {

        String receiver = commandData.receiver;
        if (receiver == null || receiver.isEmpty()) {
            sendAnswerFn.apply(createReplyOk("Message receiver not defined"), attachment.client);
            return;
        }
        if (!clients.containsKey(receiver)) {
            sendAnswerFn.apply(createReplyOk("User with login '" + receiver + "' is not connected"),
                    attachment.client);
            return;
        }

        ServerReply serverReply = createReplyOk("ok");
        serverReply.sender = commandData.sender;
        sendAnswerFn.apply(serverReply, clients.get(receiver));
        sendAnswerFn.apply(serverReply, attachment.client);
    }
}
