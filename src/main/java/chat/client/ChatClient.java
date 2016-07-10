package chat.client;

import chat.client.commands.CommandDataManager;
import chat.common.data.ServerReply;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

import static chat.client.commands.CommandDataManager.CMD_DELIMITER;
import static chat.client.commands.CommandType.LOG_IN;

/**
 * todo
 */
public class ChatClient {
    private final static int BUFFER_SIZE = 1024;
    private final static Charset UTF8 = Charset.forName("UTF-8");
    private final Gson gson = new Gson();

    @Autowired
    private CommandDataManager commandDataManager;

    private String host;
    private int port;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void start() {
        SocketAddress address = new InetSocketAddress(host, port);
        try {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            channel.connect(address).get();
            System.out.println("Connected to server");

            Attachment attachment = new Attachment();
            attachment.channel = channel;
            attachment.mainThread = Thread.currentThread();

            String login = requestUserInput("Enter login");
            attachment.login = login;
            send(commandDataManager.createCommandData(null, LOG_IN + CMD_DELIMITER + login),
                    attachment, new LogInHandler());

            attachment.mainThread.join();
        } catch (IOException | ExecutionException e) {
            System.err.println("Connection to server failed");
        } catch (InterruptedException e) {
            System.err.println("Connection closed");
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    private class LogInHandler implements CompletionHandler<Integer, Attachment> {
        @Override
        public void completed(Integer result, Attachment attachment) {
            attachment.buffer.clear();
            try {
                attachment.channel.read(attachment.buffer).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            ServerReply reply = readReply(attachment.buffer);

            System.out.println("< " + reply.message);
            if (reply.failed) {
                attachment.mainThread.interrupt();
            }

            Attachment readAttachment = new Attachment(attachment);
            attachment.channel.read(readAttachment.buffer, readAttachment, new ReadHandler());

            WriteHandler writeHandler = new WriteHandler();
            try {
                getAndSendUserInput(attachment, writeHandler);
            } catch (ClientException e) {
                System.err.println(e.getMessage());
                writeHandler.completed(result, attachment);
            }
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            exc.printStackTrace();
        }
    }

    private class ReadHandler implements CompletionHandler<Integer, Attachment> {
        @Override
        public void completed(Integer result, Attachment attachment) {
            ServerReply reply = readReply(attachment.buffer);

            if (reply != null) {
                System.out.println("< " + (reply.sender == null ? "" : (reply.sender + " > ")) + reply.message);
                if (reply.failed) {
                    attachment.mainThread.interrupt();
                }
            }

            attachment.buffer.clear();
            attachment.channel.read(attachment.buffer, attachment, this);
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            exc.printStackTrace();
        }
    }

    private class WriteHandler implements CompletionHandler<Integer, Attachment> {
        @Override
        public void completed(Integer result, Attachment attachment) {
            try {
                getAndSendUserInput(attachment, this);
            } catch (ClientException e) {
                System.err.println(e.getMessage());
                completed(result, attachment);
            }
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            exc.printStackTrace();
        }
    }

    private class Attachment {
        AsynchronousSocketChannel channel;
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        Thread mainThread;
        String login;

        Attachment() {
        }

        Attachment(Attachment src) {
            channel = src.channel;
            mainThread = src.mainThread;
            login = src.login;
        }
    }

    private ServerReply readReply(ByteBuffer buffer) {
        buffer.flip();
        int limit = buffer.limit();
        byte[] data = new byte[limit];
        buffer.get(data, 0, limit);
        return gson.fromJson(new String(data, UTF8), ServerReply.class);
    }

    private void send(byte[] data, Attachment attachment, CompletionHandler<Integer, Attachment> handler) {
        attachment.buffer.clear();
        attachment.buffer.put(data);
        attachment.buffer.flip();
        attachment.channel.write(attachment.buffer, attachment, handler);
    }

    private String requestUserInput(String userMessage) {
        System.out.println(userMessage);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private void getAndSendUserInput(Attachment attachment, WriteHandler writeHandler) throws ClientException {
        String msg = requestUserInput("Enter command 'sendToAll#[message]', 'getServerTime#', " +
                "sendToUser#[userLogin]#[message]");
        byte[] data = commandDataManager.createCommandData(attachment.login, msg);
        send(data, attachment, writeHandler);
    }

}
