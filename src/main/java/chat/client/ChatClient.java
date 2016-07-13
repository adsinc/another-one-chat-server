package chat.client;

import chat.client.commands.CommandDataManager;
import chat.common.data.ServerReply;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Iterator;

import static chat.client.commands.CommandDataManager.CMD_DELIMITER;
import static chat.client.commands.CommandType.LOG_IN;

/**
 * Main chat client bean
 */
public class ChatClient {
    private final static int BUFFER_SIZE = 1024;

    private String host;
    private int port;
    private long timeout;
    private Selector selector;
    private SocketChannel channel;
    private String login;
    private final Gson gson = new Gson();
    private final Charset UTF8 = Charset.forName("UTF-8");

    @Autowired
    private CommandDataManager commandDataManager;

    private volatile byte[] message;

    /**
     * Thread for nonblocking user input reading
     */
    private final Thread inputReader = new Thread(() -> {
        System.out.println("Command formats: 'sendToAll#[message]', 'getServerTime#', " +
                "'sendToUser#[userLogin]#[message]'");
        while (!Thread.currentThread().isInterrupted()) {
            try {
                String input = requestUserInput(">");
                requestWrite(commandDataManager.createCommandData(login, input), channel);
            } catch (ClosedChannelException e) {
                Thread.currentThread().interrupt();
            } catch (ClientException e) {
                System.out.println(e.getMessage());
            }
        }
    });

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    /**
     * Starting server
     */
    public void start() {
        login = requestUserInput("Enter login\n>");
        try {
            channel = SocketChannel.open();
            channel.configureBlocking(false);
            selector = Selector.open();

            channel.register(selector, SelectionKey.OP_CONNECT);
            channel.connect(new InetSocketAddress(host, port));

            while (!Thread.interrupted()) {
                selector.select(timeout);
                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    try {
                        SelectionKey key = keys.next();
                        keys.remove();

                        if (!key.isValid()) {
                            continue;
                        }
                        if (key.isConnectable()) {
                            System.out.println("Connected to server");
                            connect(key, login);
                        }
                        if (key.isWritable()) {
                            write(key);
                        }
                        if (key.isReadable()) {
                            read(key);
                        }
                    } catch (ClientException e) {
                        System.err.println(e.getMessage());
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
            try {
                inputReader.interrupt();
                inputReader.join();
            } catch (InterruptedException e) {
                System.err.println("Error on inputReader stream interrupt: " + e.getMessage());
            }
        }
    }

    private void requestWrite(byte[] msgData, SocketChannel channel) throws ClosedChannelException {
        message = msgData;
        channel.register(selector, SelectionKey.OP_WRITE);
    }

    private void connect(SelectionKey key, String login) throws IOException, ClientException {
        SocketChannel channel = (SocketChannel) key.channel();
        if (channel.isConnectionPending()) {
            channel.finishConnect();
        }
        channel.configureBlocking(false);
        requestWrite(commandDataManager.createCommandData(null, LOG_IN + CMD_DELIMITER + login), channel);
        inputReader.start();
    }

    private void write(SelectionKey key) throws IOException {
        SocketChannel channel = (SocketChannel) key.channel();
        channel.write(ByteBuffer.wrap(message));
        message = null;
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
        if (readLength == -1) {
            System.err.println("Nothing to read from server.");
            channel.close();
            key.cancel();
            return;
        }
        buffer.flip();
        byte[] buff = new byte[readLength];
        buffer.get(buff, 0, readLength);
        String json = new String(buff, UTF8);
        ServerReply serverReply = gson.fromJson(json, ServerReply.class);
        if (serverReply.failed) {
            System.err.println("Error. Client must be closed");
        } else {
            System.out.println((serverReply.sender == null
                    ? "*Server message* " : "[" + serverReply.sender + "] ")
                    + serverReply.message);
        }
    }

    private String requestUserInput(String userMessage) {
        System.out.print(userMessage);
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        try {
            return reader.readLine();
        } catch (IOException e) {
            System.err.println("Read input IO error: " + e.getMessage());
        }
        return "";
    }

}
