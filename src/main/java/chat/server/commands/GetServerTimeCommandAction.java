package chat.server.commands;

import chat.common.data.CommandData;
import chat.server.ChatServer;

import java.nio.channels.AsynchronousSocketChannel;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiFunction;

import static chat.common.data.ServerReply.createReplyOk;

public class GetServerTimeCommandAction implements CommandAction {
    @Override
    public void execute(CommandData commandData, ChatServer.Attachment attachment,
                        Map<String, ChatServer.Attachment> clients,
                        BiFunction<Object, AsynchronousSocketChannel, Void> sendAnswerFn) {
        sendAnswerFn.apply(createReplyOk("Server time: " + LocalDateTime.now().toString()), attachment.client);
    }
}
