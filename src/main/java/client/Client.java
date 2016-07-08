package client;

import com.google.gson.Gson;
import commands.CommandData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutionException;

/**
 * todo
 */
public class Client {
    public static void main(String[] args) throws IOException, InterruptedException {
        SocketAddress address = new InetSocketAddress("localhost", 7777);
        try {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            channel.connect(address).get();
            System.out.println("Connected to server");

            Attachment attachment = new Attachment();
            attachment.channel = channel;
            attachment.buffer = ByteBuffer.allocate(1024);
            attachment.mainThread = Thread.currentThread();

            attachment.buffer.put(createSendToAllCommand("Hello"));
            attachment.buffer.flip();

            channel.write(attachment.buffer, attachment, new CompletionHandler<Integer, Attachment>() {
                @Override
                public void completed(Integer result, Attachment attachment) {
                    if (attachment.isRead) {
                        attachment.buffer.flip();
                        int limit = attachment.buffer.limit();
                        byte[] data = new byte[limit];
                        attachment.buffer.get(data, 0, limit);
                        System.out.println("Server response" + new String(data, Charset.forName("UTF-8")));

                        attachment.buffer.clear();
                        data = createSendToAllCommand("Hello");
                        attachment.buffer.put(data);
                        attachment.buffer.flip();
                        attachment.isRead = false;
                        attachment.channel.write(attachment.buffer, attachment, this);
                    } else {
                        attachment.isRead = true;
                        attachment.buffer.clear();
                        attachment.channel.read(attachment.buffer, attachment, this);
                    }
                }

                @Override
                public void failed(Throwable exc, Attachment attachment) {
                    exc.printStackTrace();
                }
            });

            attachment.mainThread.join();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    static class Attachment {
        AsynchronousSocketChannel channel;
        ByteBuffer buffer;
        Thread mainThread;
        boolean isRead;
    }

    private static byte[] createSendToAllCommand(String msg) {
        Gson gson = new Gson();
        CommandData command = new CommandData();
        command.commandName = "sendToAll";
        command.sender = "alex";
        command.message = msg;
        return gson.toJson(command).getBytes(Charset.forName("UTF-8"));
    }
}
