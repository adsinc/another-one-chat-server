package client;

import com.google.gson.Gson;
import commands.CommandData;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.ExecutionException;

/**
 * todo
 */
public class Client {
    public static void main(String[] args) {
        SocketAddress address = new InetSocketAddress("localhost", 7777);
        try {
            AsynchronousSocketChannel channel = AsynchronousSocketChannel.open();
            channel.connect(address).get();
            Gson gson = new Gson();
            CommandData command = new CommandData();
            command.commandName = "sendToAll";
            command.sender = "alex";
            command.message = "hello to all";
            ByteBuffer buffer = ByteBuffer.wrap(gson.toJson(command).getBytes());
            channel.write(buffer).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
