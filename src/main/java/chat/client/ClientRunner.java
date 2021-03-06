package chat.client;

import org.springframework.context.support.GenericXmlApplicationContext;

/**
 * Runner for chat client application
 */
public class ClientRunner {
    public static void main(String[] args) {
        GenericXmlApplicationContext ctx = new GenericXmlApplicationContext("classpath:client-context.xml");
        ChatClient chatClient = ctx.getBean("chatClient", ChatClient.class);
        chatClient.start();
    }
}
