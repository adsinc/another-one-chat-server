package client;

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
            ByteBuffer buffer = ByteBuffer.wrap("Hello".getBytes());
            channel.write(buffer).get();
            Thread.sleep(5000);
            channel.write(ByteBuffer.wrap("111".getBytes())).get();
        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
