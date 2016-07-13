package chat.server.commands;

import chat.common.data.CommandData;
import com.google.gson.Gson;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

/**
 * Bean helps to parse json data received from client and find appropriate command actions
 */
public class CommandManager implements BeanFactoryAware {
    private ConfigurableListableBeanFactory factory;
    private Gson parser = new Gson();

    /**
     * Parse json command data into appropriate object
     *
     * @param json received from client json command
     * @return parsed command data
     */
    public CommandData parseCommand(String json) {
        return parser.fromJson(json, CommandData.class);
    }

    /**
     * Validate command data
     * @param commandData command data
     * @return true if appropriate command action exists
     */
    public boolean validate(CommandData commandData) {
        for (String name : factory.getBeanNamesForType(CommandAction.class)) {
            if (name.equals(commandData.commandName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Return command action buy command data.
     * @param commandData command data
     * @return command action
     */
    public CommandAction getCommandAction(CommandData commandData) {
        return factory.getBean(commandData.commandName, CommandAction.class);
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        factory = (ConfigurableListableBeanFactory) beanFactory;
    }
}
