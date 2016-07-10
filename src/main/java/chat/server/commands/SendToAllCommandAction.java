package chat.server.commands;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;
import chat.server.ChatServer;

import java.nio.channels.AsynchronousSocketChannel;
import java.util.Map;
import java.util.function.BiFunction;

import static chat.common.data.ServerReply.createReplyOk;

public class SendToAllCommandAction implements CommandAction {
    @Override
    public void execute(CommandData commandData, ChatServer.Attachment attachment,
                        Map<String, ChatServer.Attachment> clients,
                        BiFunction<ServerReply, AsynchronousSocketChannel, Void> sendAnswerFn) {

        ServerReply serverReply = createReplyOk(commandData.message);
        serverReply.sender = commandData.sender;

        clients.values().stream().forEach(att -> att.replyFn.apply(serverReply, att.client));
    }
}
