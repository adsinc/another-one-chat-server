package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.function.BiFunction;

import static chat.common.data.ServerReply.createReplyOk;

/**
 * Send message from sender to receiver client action
 */
public class SendToUserCommandAction implements CommandAction {
    @Override
    public void execute(CommandData commandData,
                        SocketChannel client, Map<String, SocketChannel> loginToClient,
                        BiFunction<SocketChannel, ServerReply, Void> replyCallBack) {

        String receiver = commandData.receiver;
        if (receiver == null || receiver.isEmpty()) {
            replyCallBack.apply(client, createReplyOk("Message receiver not defined"));
            return;
        }
        if (!loginToClient.containsKey(receiver)) {
            replyCallBack.apply(client, createReplyOk("User with login '" + receiver + "' is not connected"));
            return;
        }

        ServerReply serverReply = createReplyOk(commandData.message);
        serverReply.sender = commandData.sender;

        SocketChannel receiverChannel = loginToClient.get(receiver);
        replyCallBack.apply(receiverChannel, serverReply);
        replyCallBack.apply(client, serverReply);
    }
}
