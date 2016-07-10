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
        sendAnswerFn.apply(createReplyOk("ok"), attachment.client);
    }
}
