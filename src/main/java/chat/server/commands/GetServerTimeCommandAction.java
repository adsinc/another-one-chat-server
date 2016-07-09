package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;
import chat.common.data.SimpleOkReply;
import chat.server.ChatServer;

import java.nio.channels.AsynchronousSocketChannel;
import java.time.LocalDateTime;
import java.util.Map;

public class GetServerTimeCommandAction implements CommandAction {
    @Override
    public ServerReply execute(CommandData commandData, ChatServer.Attachment attachment,
                               Map<String, AsynchronousSocketChannel> clients) {
        return new SimpleOkReply("Server time: " + LocalDateTime.now().toString());
    }
}
