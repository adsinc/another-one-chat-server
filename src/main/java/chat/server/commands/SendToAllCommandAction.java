package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.function.BiFunction;

import static chat.common.data.ServerReply.createReplyOk;

/**
 * Send message from sender to all connected clients action.
 */
public class SendToAllCommandAction implements CommandAction {
    @Override
    public void execute(CommandData commandData,
                        SocketChannel client, Map<String, SocketChannel> loginToClient,
                        BiFunction<SocketChannel, ServerReply, Void> replyCallBack) {

        ServerReply serverReply = createReplyOk(commandData.message);
        serverReply.sender = commandData.sender;

        loginToClient.forEach((login, channel) -> replyCallBack.apply(channel, serverReply));
    }
}
