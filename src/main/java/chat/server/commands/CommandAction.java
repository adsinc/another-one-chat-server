package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * Interface for actions executed on command receive
 */
public interface CommandAction {
    /**
     * Execute action for appropriate command
     *
     * @param commandData   CommandData object received from client
     * @param client        client's channel
     * @param loginToClient map of logins and appropriate channels
     * @param replyCallBack callback used to write ServerReply to appropriate channel
     */
    void execute(CommandData commandData,
                 SocketChannel client, Map<String, SocketChannel> loginToClient,
                 BiFunction<SocketChannel, ServerReply, Void> replyCallBack);
}
