package chat.common.data;

/**
 *
 */
public class SimpleFailedReply implements ServerReply {
    private final String msg;

    public SimpleFailedReply(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public boolean isFailed() {
        return true;
    }
}
