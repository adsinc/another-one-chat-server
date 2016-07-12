package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.function.BiFunction;

public class SendToAllCommandAction implements CommandAction {
    @Override
    public void execute(CommandData commandData,
                        SocketChannel client, Map<String, SocketChannel> loginToClient,
                        BiFunction<SocketChannel, ServerReply, Void> replyCallBack) {
//
//        ServerReply serverReply = createReplyOk(commandData.message);
//        serverReply.sender = commandData.sender;
//
//        replyCallBack.apply(serverReply, attachment.client);
//
//        if (loginToClient.get(commandData.sender).equals(attachment)) {
//            loginToClient.forEach((login, att) -> {
//                if (!login.equals(commandData.sender)) {
//                    att.replyFn.apply(commandData, att.client);
//                }
//            });
//        }
    }
}
