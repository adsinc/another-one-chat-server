package chat.server;

import org.springframework.context.support.GenericXmlApplicationContext;

/**
 *
 */
public class ServerRunner {
    public static void main(String[] args) {
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:context.xml");
        ChatServer chatServer = ctx.getBean("chatServer", ChatServer.class);
        chatServer.start();
    }
}
