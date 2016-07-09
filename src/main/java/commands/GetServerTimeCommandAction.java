package commands;

import com.google.gson.Gson;

import java.time.LocalDateTime;

public class GetServerTimeCommandAction implements CommandAction {
    @Override
    public String execute(CommandData commandData) {
        IncomingMessage answer = new IncomingMessage();
        answer.message = "Server time: " + LocalDateTime.now().toString();
        return new Gson().toJson(answer);
    }
}
