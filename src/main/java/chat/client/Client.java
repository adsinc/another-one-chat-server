package chat.client;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;
import com.google.gson.Gson;

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

import static chat.common.data.CommandType.*;

/**
 * todo
 */
public class Client {
    private final static int BUFFER_SIZE = 1024;
    private final static Charset UTF8 = Charset.forName("UTF-8");
    private final Gson gson = new Gson();

    public static void main(String[] args) throws IOException, InterruptedException {
        new Client().start();
    }

    public void start() {
        SocketAddress address = new InetSocketAddress("localhost", 7777);
        try {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            channel.connect(address).get();
            System.out.println("Connected to server");

            Attachment attachment = new Attachment();
            attachment.channel = channel;
            attachment.mainThread = Thread.currentThread();

            send(createLogInCommand(requestUserInput("Enter login")), attachment, new LogInHandler());

            attachment.mainThread.join();
        } catch (IOException | ExecutionException e) {
            System.err.println("Connection to server failed");
        } catch (InterruptedException e) {
            System.out.println("Connection closed");
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

            System.out.println("Server response: " + reply.message);
            if (reply.failed) {
                attachment.mainThread.interrupt();
            }

            Attachment readAttachment = new Attachment(attachment);
            attachment.channel.read(readAttachment.buffer, readAttachment, new ReadHandler());

            getAndSendUserInput(attachment, new WriteHandler());
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
                System.out.println("Server response: " + reply.sender + " > " + reply.message);
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
            getAndSendUserInput(attachment, this);
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            exc.printStackTrace();
        }
    }

    private class Attachment {
        Attachment() {
        }

        Attachment(Attachment src) {
            channel = src.channel;
            mainThread = src.mainThread;
        }

        AsynchronousSocketChannel channel;
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        Thread mainThread;
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


    private byte[] createSendToAllCommand(String msg) {
        CommandData command = new CommandData();
        command.commandName = SEND_TO_ALL;
        command.sender = "alex";
        command.visibleForAll = true;
        command.message = msg;
        return gson.toJson(command).getBytes(UTF8);
    }

    private byte[] createSendToUserCommand(String msg) {
        CommandData command = new CommandData();
        command.commandName = SEND_TO_USER;
        command.sender = "alex";
        command.receiver = "roman";
        command.message = msg;
        return gson.toJson(command).getBytes(UTF8);
    }

    private byte[] createGetServerTimeCommand() {
        CommandData command = new CommandData();
        command.commandName = GET_SERVER_TIME;
        return gson.toJson(command).getBytes(UTF8);
    }

    private byte[] createLogInCommand(String login) {
        CommandData command = new CommandData();
        command.commandName = LOG_IN;
        command.sender = login;
        return gson.toJson(command).getBytes(UTF8);
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

    private void getAndSendUserInput(Attachment attachment, WriteHandler writeHandler) {
        String msg = requestUserInput("Enter command:");
        byte[] data = msg.isEmpty() ? createGetServerTimeCommand() : createSendToAllCommand(msg);
        send(data, attachment, writeHandler);
    }

}
