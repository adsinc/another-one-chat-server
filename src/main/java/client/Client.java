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
            channel.write(createSendToAllCommand("hello there")).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    private static ByteBuffer createSendToAllCommand(String msg) {
        Gson gson = new Gson();
        CommandData command = new CommandData();
        command.commandName = "sendToAll";
        command.sender = "alex";
        command.message = msg;
        return ByteBuffer.wrap(gson.toJson(command).getBytes());
    }
}
