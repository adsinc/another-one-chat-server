package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;

import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.function.BiFunction;

public class SendToUserCommandAction implements CommandAction {
    @Override
    public void execute(CommandData commandData,
                        SocketChannel client, Map<String, SocketChannel> loginToClient,
                        BiFunction<SocketChannel, ServerReply, Void> replyCallBack) {

//        String receiver = commandData.receiver;
//        if (receiver == null || receiver.isEmpty()) {
//            replyCallBack.apply(createReplyOk("Message receiver not defined"), attachment.client);
//            return;
//        }
//        if (!loginToClient.containsKey(receiver)) {
//            replyCallBack.apply(createReplyOk("User with login '" + receiver + "' is not connected"),
//                    attachment.client);
//            return;
//        }
//
//        ServerReply serverReply = createReplyOk(commandData.message);
//        serverReply.sender = commandData.sender;
//
//        ChatServerOld.Attachment receiverAtt = loginToClient.get(receiver);
//        receiverAtt.replyFn.apply(serverReply, receiverAtt.client);
//
//        replyCallBack.apply(serverReply, attachment.client);
    }
}
