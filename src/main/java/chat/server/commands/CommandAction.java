package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * todo
 */
public interface CommandAction {
    void execute(CommandData commandData,
                 SocketChannel client, Map<String, SocketChannel> loginToClient,
                 BiFunction<SocketChannel, ServerReply, Void> replyCallBack);
}
