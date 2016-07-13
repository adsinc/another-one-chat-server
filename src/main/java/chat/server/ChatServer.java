package chat.server;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;
import chat.server.commands.CommandAction;
import chat.server.commands.CommandManager;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Main chat server bean
 */
public class ChatServer {

    private final static int BUFFER_SIZE = 1024;

    private final Charset UTF8 = Charset.forName("UTF-8");

    private Integer port;
    private long timeout;

    @Autowired
    private CommandManager commandManager;

    private Selector selector;
    private ServerSocketChannel serverChannel;
    private Map<SocketChannel, byte[]> clientsMessages = new HashMap<>();
    private Map<String, SocketChannel> loginToClient = new HashMap<>();

    public void setPort(Integer port) {
        this.port = port;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Start server
     */
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
        clientChannel.register(selector, SelectionKey.OP_READ);
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel client = (SocketChannel) key.channel();
        byte[] msg = clientsMessages.remove(client);
        client.write(ByteBuffer.wrap(msg));
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
        if (readLength == -1) {
            System.out.println("Nothing was there to be read, closing connection");
            client.close();
            key.cancel();
            return;
        }

        buffer.flip();
        byte[] data = new byte[readLength];
        buffer.get(data, 0, readLength);

        String json = new String(data, UTF8);
        System.out.println("Received: " + json);

        try {
            CommandData commandData = commandManager.parseCommand(json);
            if (commandManager.validate(commandData)) {
                CommandAction commandAction = commandManager.getCommandAction(commandData);
                commandAction.execute(commandData, client, loginToClient, this::send);
            } else {
                send(client, ServerReply.createReplyFailed("Incorrect command: '" + json + "'"));
            }
        } catch (JsonParseException e) {
            System.err.println("Can not parse command '" + json + "'");
        }
    }

    private Void send(SocketChannel socketChannel, ServerReply serverReply) {
        clientsMessages.put(socketChannel, new Gson().toJson(serverReply).getBytes(UTF8));
        socketChannel.keyFor(selector).interestOps(SelectionKey.OP_WRITE);
        return null;
    }
}
