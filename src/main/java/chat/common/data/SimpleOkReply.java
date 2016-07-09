package chat.common.data;

/**
 *
 */
public class SimpleOkReply implements ServerReply {
    private final String msg;

    public SimpleOkReply(String msg) {
        this.msg = msg;
    }

    @Override
    public String getMessage() {
        return msg;
    }

    @Override
    public boolean isFailed() {
        return false;
    }
}
