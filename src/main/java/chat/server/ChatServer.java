package chat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 *
 */
public class ChatServer {

    private final static int BUFFER_SIZE = 1024;

    private Integer port;
    private long timeout;

    private Selector selector;
    private ServerSocketChannel serverChannel;
    private Map<SocketChannel, byte[]> clientsMessages = new HashMap<>();

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public void start() {
        Objects.requireNonNull(port, "Port is not defined");
        try {
            selector = Selector.open();
            serverChannel = ServerSocketChannel.open();
            serverChannel.configureBlocking(false);
            serverChannel.socket().bind(new InetSocketAddress(port));
            serverChannel.register(selector, SelectionKey.OP_ACCEPT);
            acceptConnections();
        } catch (IOException e) {
            System.err.println("Server can not be started: " + e.getMessage());
        }
    }

    private void acceptConnections() {
        System.out.println("Listening port " + port);
        try {
            while (!Thread.currentThread().isInterrupted()) {
                selector.select(timeout);
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();

                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();

                    if (!key.isValid()) {
                        continue;
                    }

                    if (key.isAcceptable()) {
                        System.out.println("Accepting new connection");
                        accept(key);
                    }

                    if (key.isWritable()) {
                        System.out.println("Write");
                        write(key);
                    }

                    if (key.isReadable()) {
                        System.out.println("Read");
                        read(key);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Internal server IO error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void closeConnection() {
        System.out.println("Closing connections");
        if (selector != null) {
            try {
                selector.close();
                serverChannel.socket().close();
                serverChannel.close();
            } catch (IOException e) {
                System.err.println("Error on connection closing: " + e.getMessage());
            }
        }
    }

    private void accept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false);
        clientChannel.register(selector, SelectionKey.OP_WRITE);
        // todo auth
        byte[] msg = "Logged as Troll".getBytes();
        clientsMessages.put(clientChannel, msg);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        byte[] msg = clientsMessages.remove(client);
        client.write(ByteBuffer.wrap(msg));
        // todo intesting place maybe dont do it
        key.interestOps(SelectionKey.OP_READ);
    }

    private void read(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        int readLength;
        try {
            readLength = client.read(buffer);
        } catch (IOException e) {
            System.err.println("Error on reading data from channel. Connection closed");
            key.cancel();
            client.close();
            return;
        }
        // todo think where
        if (readLength == -1) {
            System.out.println("Nothing was there to be read, closing connection");
            client.close();
            key.cancel();
            return;
        }

        buffer.flip();
        byte[] data = new byte[readLength];
        buffer.get(data, 0, readLength);
        System.out.println("Received: " + new String(data));

        //todo this place!
        echo(key, data);
    }

    private void echo(SelectionKey key, byte[] data) {
        SocketChannel socketChannel = (SocketChannel) key.channel();
        clientsMessages.put(socketChannel, data);
        key.interestOps(SelectionKey.OP_WRITE);
    }
}
