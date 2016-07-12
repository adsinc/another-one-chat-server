package chat.client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * todo
 */
public class ChatClient {
    private final static int BUFFER_SIZE = 1024;

    private String host;
    private int port;
    private long timeout;
    private Selector selector;

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void start() {
        try {
            SocketChannel channel = SocketChannel.open();
            channel.configureBlocking(false);
            selector = Selector.open();

            channel.register(selector, SelectionKey.OP_CONNECT);
            channel.connect(new InetSocketAddress(host, port));

            while (!Thread.interrupted()) {
                selector.select(timeout);
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isConnectable()) {
                        System.out.println("Connected to server");
                        connect(key);
                    }

                    if (key.isWritable()) {
                        write(key);
                    }

                    if (key.isReadable()) {
                        read(key);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("IO error: " + e.getMessage());
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
                System.err.println("Error on closing selector: " + e.getMessage());
            }
        }
    }

    private void connect(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        channel.configureBlocking(false);
        // todo log in?
        channel.register(selector, SelectionKey.OP_WRITE);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        // todo fix it
        channel.write(ByteBuffer.wrap("message to server".getBytes()));
        key.interestOps(SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        buffer.clear();
        int readLength;
        try {
            readLength = channel.read(buffer);
        } catch (IOException e) {
            System.err.println("Error on reading data from server. Connection closed");
            key.cancel();
            channel.close();
            return;
        }
        //todo think!
        if (readLength == -1) {
            System.err.println("Nothing was read from server.");
            channel.close();
            key.cancel();
            return;
        }
        buffer.flip();
        byte[] buff = new byte[readLength];
        buffer.get(buff, 0, readLength);
        System.out.println("Server said: " + new String(buff));
    }
}
