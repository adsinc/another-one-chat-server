package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;

import java.nio.channels.SocketChannel;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.function.BiFunction;

import static chat.common.data.ServerReply.createReplyOk;

/**
 * Action return server time to sender client
 */
public class GetServerTimeCommandAction implements CommandAction {
    @Override
    public void execute(CommandData commandData,
                        SocketChannel client, Map<String, SocketChannel> loginToClient,
                        BiFunction<SocketChannel, ServerReply, Void> replyCallBack) {
        replyCallBack.apply(client, createReplyOk("Server time: " + LocalDateTime.now().toString()));
    }
}
