package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;
import chat.server.ChatServer;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;

/**
 * todo
 */
public interface CommandAction {
    ServerReply execute(CommandData commandData, ChatServer.Attachment attachment,
                        Map<String, AsynchronousSocketChannel> clients);
}
