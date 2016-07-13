package chat.server.commands;

import chat.client.commands.CommandType;
import chat.common.data.CommandData;
import com.google.gson.JsonParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:server-context.xml")
public class CommandManagerTest {

    @Autowired
    private CommandManager commandManager;

    private final Map<String, CommandData> testCommands = new HashMap<>();

    {
        testCommands.put("{}", new CommandData());
        testCommands.put("{\"commandName\": \"c\", \"sender\": \"s\", \"message\": \"m\"}",
                new CommandData("c", "s", "m", null));
        testCommands.put("{\"commandName\": \"c\", \"sender\": \"s\", \"message\": \"m\",\"receiver\": \"r\" }",
                new CommandData("c", "s", "m", "r"));
    }

    @Test
    public void testParseCommand() throws Exception {
        testCommands.forEach((json, cd) -> assertEquals(cd, commandManager.parseCommand(json)));
    }

    @Test(expected = JsonParseException.class)
    public void testIncorrectCommand() throws Exception {
        commandManager.parseCommand("test");
    }

    @Test(expected = JsonParseException.class)
    public void testCommandWithAdditionalFields() throws Exception {
        commandManager.parseCommand("{\"commandName\": \"c\", \"sender\": \"s\", \"message\": \"m\"," +
                "\"receiver\": \"r\" \"incorrectField\": \"r\" }");
    }

    /**
     * Validate should return false in bean with name equals to commandName does not exists
     */
    @Test
    public void testValidate() throws Exception {
        CommandData commandData = new CommandData();
        commandData.commandName = "notExistBeanName";
        assertFalse(commandManager.validate(commandData));
        commandData.commandName = CommandType.SEND_TO_ALL;
        assertTrue(commandManager.validate(commandData));
    }

    @Test
    public void testGetCommandAction() throws Exception {
        CommandData commandData = new CommandData();
        commandData.commandName = CommandType.SEND_TO_USER;
        CommandAction action = commandManager.getCommandAction(commandData);
        assertEquals(action.getClass(), SendToUserCommandAction.class);
    }

}