package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;
import chat.server.ChatServer;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.function.BiFunction;

import static chat.common.data.ServerReply.createReplyOk;

public class SendToAllCommandAction implements CommandAction {
    @Override
    public void execute(CommandData commandData, ChatServer.Attachment attachment,
                        Map<String, ChatServer.Attachment> clients,
                        BiFunction<Object, AsynchronousSocketChannel, Void> sendAnswerFn) {

        ServerReply serverReply = createReplyOk(commandData.message);
        serverReply.sender = commandData.sender;

        sendAnswerFn.apply(serverReply, attachment.client);

        if (clients.get(commandData.sender).equals(attachment)) {
            clients.forEach((login, att) -> {
                if (!login.equals(commandData.sender)) {
                    att.replyFn.apply(commandData, att.client);
                }
            });
        }
    }
}
