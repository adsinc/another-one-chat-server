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
            attachment.buffer = ByteBuffer.allocate(1024);
            attachment.mainThread = Thread.currentThread();

            attachment.buffer.put(createLogInCommand(requestUserInput("Enter login")));
            attachment.buffer.flip();

            channel.write(attachment.buffer, attachment, new LogInHandler());
            attachment.mainThread.join();
        } catch (IOException | ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            System.out.println("Connection closed");
        }
    }

    private class LogInHandler implements CompletionHandler<Integer, Attachment> {
        @Override
        public void completed(Integer result, Attachment attachment) {
            attachment.isRead = true;
            attachment.buffer.clear();
            try {
                attachment.channel.read(attachment.buffer).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            ServerReply reply = new Gson().fromJson(readString(attachment.buffer), ServerReply.class);

            System.out.println("Server response: " + reply.message);
            if (reply.failed) {
                attachment.mainThread.interrupt();
            }

            Attachment readAttachment = new Attachment();
            readAttachment.buffer = ByteBuffer.allocate(1024);
            readAttachment.channel = attachment.channel;
            readAttachment.mainThread = attachment.mainThread;
            attachment.channel.read(attachment.buffer, readAttachment, new ReadHandler());

            String msg = requestUserInput("Enter command:");

            attachment.buffer.clear();
            byte[] data = msg.isEmpty() ? createGetServerTimeCommand() : createSendToAllCommand(msg);
            attachment.buffer.put(data);
            attachment.buffer.flip();
            attachment.isRead = false;
            attachment.channel.write(attachment.buffer, attachment, new WriteHandler());
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            exc.printStackTrace();
        }
    }

    private String readString(ByteBuffer buffer) {
        buffer.flip();
        int limit = buffer.limit();
        byte[] data = new byte[limit];
        buffer.get(data, 0, limit);
        return new String(data, UTF8);
    }

//    private String toJson() {
//
//    }

    private class ReadHandler implements CompletionHandler<Integer, Attachment> {
        @Override
        public void completed(Integer result, Attachment attachment) {
            ServerReply reply = new Gson().fromJson(readString(attachment.buffer), ServerReply.class);

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
            String msg = requestUserInput("Enter command:");
            attachment.buffer.clear();
            byte[] data = msg.isEmpty() ? createGetServerTimeCommand() : createSendToAllCommand(msg);
            attachment.buffer.put(data);
            attachment.buffer.flip();
            attachment.isRead = false;
            attachment.channel.write(attachment.buffer, attachment, this);
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            exc.printStackTrace();
        }
    }

    static class Attachment {
        AsynchronousSocketChannel channel;
        ByteBuffer buffer;
        Thread mainThread;
        boolean isRead;
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

}
