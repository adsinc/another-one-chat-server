package chat.server.commands;

import chat.common.data.CommandData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    @Test
    public void testValidate() throws Exception {
        assertTrue(false);
    }

    @Test
    public void testGetCommandAction() throws Exception {
        assertTrue(false);
    }

}