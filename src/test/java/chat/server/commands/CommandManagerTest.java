package chat.server.commands;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(locations = "classpath:server-context.xml")
public class CommandManagerTest {

    @Autowired
    private CommandManager commandManager;

    @Test
    public void testParseCommand() throws Exception {
        //todo
    }

    @Test
    public void testValidate() throws Exception {
        //todo
    }

    @Test
    public void testGetCommandAction() throws Exception {
        //todo
    }

}