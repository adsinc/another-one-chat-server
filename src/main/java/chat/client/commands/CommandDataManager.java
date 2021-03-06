package chat.client.commands;

import chat.client.ClientException;
import com.google.gson.Gson;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import java.nio.charset.Charset;

/**
 * Bean help to create byte array from user input commands.
 */
public class CommandDataManager implements BeanFactoryAware {
    public final static String CMD_DELIMITER = "#";
    private final static Charset UTF8 = Charset.forName("UTF-8");
    private final Gson gson = new Gson();
    private ConfigurableListableBeanFactory factory;

    /**
     * Parse user input and create byte array representation of CommandData object
     *
     * @param sender    sender login
     * @param userInput command from user
     * @return byte array representation of CommandData object
     */
    public byte[] createCommandData(String sender, String userInput) throws ClientException {
        CommandType commandType = extractCommandType(userInput);
        String message = userInput.substring((commandType.getName() + CMD_DELIMITER).length());
        return gson.toJson(commandType.createCommandData(sender, message)).getBytes(UTF8);
    }

    /**
     * Parse user input and extract CommandType
     *
     * @param userInput command from user
     * @return appropriate to user input CommandType object
     */
    private CommandType extractCommandType(String userInput) throws ClientException {
        if (!userInput.contains(CMD_DELIMITER))
            throw new ClientException("Incorrect command format.");
        String[] parts = userInput.split(CMD_DELIMITER);
        String typeName = parts[0];
        for (String name : factory.getBeanNamesForType(CommandType.class)) {
            if (name.equals(typeName)) {
                return factory.getBean(typeName, CommandType.class);
            }
        }
        throw new ClientException("Command '" + typeName + "' does not exist");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        factory = (ConfigurableListableBeanFactory) beanFactory;
    }
}
