package chat.server;

import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Runner for Chat Server application
 */
public class ServerRunner {
    public static void main(String[] args) {
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:server-context.xml");
        ChatServer chatServer = ctx.getBean("chatServer", ChatServer.class);
        chatServer.start();
    }
}
