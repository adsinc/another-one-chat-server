package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;
import chat.server.ChatServer;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * todo
 */
public interface CommandAction {
    void execute(CommandData commandData, ChatServer.Attachment attachment,
                 Map<String, ChatServer.Attachment> clients,
                 BiFunction<ServerReply, AsynchronousSocketChannel, Void> sendAnswerFn);
}
