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
                        ChatServer.Attachment attachment, Map<String, ChatServer.Attachment> clients,
                        BiFunction<Object, AsynchronousSocketChannel, Void> sendAnswerFn) {

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

        ServerReply serverReply = createReplyOk(commandData.message);
        serverReply.sender = commandData.sender;

        ChatServer.Attachment receiverAtt = clients.get(receiver);
        receiverAtt.replyFn.apply(serverReply, receiverAtt.client);

        sendAnswerFn.apply(serverReply, attachment.client);
    }
}
