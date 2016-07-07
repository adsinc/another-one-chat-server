package chat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

/**
 * todo
 */
public class ChatServer {

    private int port;

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
            newAttachment.buffer = ByteBuffer.allocate(512);
            newAttachment.readSb = new StringBuilder();
            client.read(newAttachment.buffer, newAttachment, new ReadHandler());
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            //todo
        }
    }

    private class ReadHandler implements CompletionHandler<Integer, Attachment> {
        @Override
        public void completed(Integer result, Attachment attachment) {
            if (result != -1) {
                ByteBuffer buffer = attachment.buffer;
                buffer.flip();
                int limit = buffer.limit();
                byte[] data = new byte[limit];
                buffer.get(data, 0, limit);
                attachment.readSb.append(new String(data, Charset.forName("UTF-8")));
                attachment.buffer.rewind();
                attachment.client.read(attachment.buffer, attachment, this);
            } else {
                System.out.println("Received: " + attachment.readSb);
                try {
                    attachment.client.close();
                } catch (IOException e) {
                    // todo
                }
            }
        }

        @Override
        public void failed(Throwable exc, Attachment attachment) {
            //todo
        }
    }

    private class Attachment {
        AsynchronousServerSocketChannel server;
        AsynchronousSocketChannel client;
        ByteBuffer buffer;
        StringBuilder readSb;
    }
}

