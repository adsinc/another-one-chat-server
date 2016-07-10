package chat.common.data;

/**
 *
 */
public class ServerReply {
    public String sender;
    public String message;
    public boolean failed;

    public static ServerReply createReplyOk(String message) {
        ServerReply reply = new ServerReply();
        reply.message = message;
        return reply;
    }

    public static ServerReply createReplyFailed(String message) {
        ServerReply reply = new ServerReply();
        reply.message = message;
        reply.failed = true;
        return reply;
    }
}
