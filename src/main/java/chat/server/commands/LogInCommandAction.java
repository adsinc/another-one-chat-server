package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.function.BiFunction;

import static chat.common.data.ServerReply.createReplyFailed;
import static chat.common.data.ServerReply.createReplyOk;

/**
 * Log in action
 */
public class LogInCommandAction implements CommandAction {
    @Override
    public void execute(CommandData cmd,
                        SocketChannel client, Map<String, SocketChannel> loginToClient,
                        BiFunction<SocketChannel, ServerReply, Void> replyCallBack) {
        if (loginToClient.containsKey(cmd.sender)) {
            createReplyFailed("ChatClient already logged");
        } else {
            loginToClient.put(cmd.sender, client);
            replyCallBack.apply(client, createReplyOk("Logged as: " + cmd.sender));
        }
    }
}
