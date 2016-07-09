package chat.server;

import chat.common.data.CommandData;
import chat.common.data.ServerReply;
import chat.common.data.SimpleFailedReply;
import chat.server.commands.CommandManager;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * todo
 */
public class ChatServer {

    @Autowired
    private CommandManager commandManager;
    private int port;
    private final Map<String, AsynchronousSocketChannel> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void setPort(int port) {
        this.port = port;
    }

    public void start() {
        System.out.println("Server started");
        SocketAddress address = new InetSocketAddress(port);
        try (AsynchronousServerSocketChannel server = AsynchronousServerSocketChannel.open()) {
            server.bind(address);
            Attachment attachment = new Attachment();
            attachment.server = server;
            server.accept(attachment, new ConnectionHandler());
            Thread.currentThread().join();
        } catch (IOException e) {
            //todo
            e.printStackTrace();
        } catch (InterruptedException e) {
            //todo
            e.printStackTrace();
        }
    }

    private class ConnectionHandler implements CompletionHandler<AsynchronousSocketChannel, Attachment> {
        @Override
        public void completed(AsynchronousSocketChannel client, Attachment attachment) {
            attachment.server.accept(attachment, this);
            Attachment newAttachment = new Attachment();
            newAttachment.client = client;
            newAttachment.buffer = ByteBuffer.allocate(1024);
            newAttachment.readSb = new StringBuilder();
            newAttachment.isRead = true;
            client.read(newAttachment.buffer, newAttachment, new ReadWriteHandler());
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            //todo
        }
    }

    private class ReadWriteHandler implements CompletionHandler<Integer, Attachment> {
        @Override
        public void completed(Integer result, Attachment attachment) {
            if (result == -1) {
                try {
                    attachment.client.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                return;
            }

            if (attachment.isRead) {
                ByteBuffer buffer = attachment.buffer;
                buffer.flip();
                int limit = buffer.limit();
                byte[] data = new byte[limit];
                buffer.get(data, 0, limit);
                attachment.readSb.append(new String(data, Charset.forName("UTF-8")));
                attachment.buffer.rewind();

                if (result == buffer.capacity()) {
                    attachment.client.read(attachment.buffer, attachment, this);
                } else {
                    attachment.isRead = false;
                    System.out.println("Received: " + attachment.readSb);
                    try {
                        CommandData cmd = commandManager.parseCommand(attachment.readSb.toString());
                        System.out.println(cmd);
                        System.out.println(commandManager.validate(cmd));

                        ServerReply answer = !attachment.loggedId
                                ? new SimpleFailedReply("Client is not logged in")
                                : commandManager.getCommandAction(cmd).execute(cmd, attachment, connections);

                        attachment.buffer.clear();
                        // TODO: 08.07.16 send ACK
                        attachment.buffer.put(gson.toJson(answer).getBytes(Charset.forName("UTF-8")));
                        attachment.buffer.flip();
                        attachment.client.write(attachment.buffer, attachment, this);
                    } catch (JsonParseException e) {
                        System.out.println("Can not parse command");
                    } finally {
                        attachment.readSb.setLength(0);
                    }
                }
            } else {
                attachment.isRead = true;
                attachment.buffer.clear();
                if (!attachment.loggedId) {
                    try {
                        connections.values().remove(attachment.client);
                        attachment.client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    attachment.client.read(attachment.buffer, attachment, this);
                }
            }
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            //todo
        }
    }

    public static class Attachment {
        public AsynchronousServerSocketChannel server;
        public AsynchronousSocketChannel client;
        public ByteBuffer buffer;
        public StringBuilder readSb;
        public boolean isRead;
        public boolean loggedId;
    }
}

