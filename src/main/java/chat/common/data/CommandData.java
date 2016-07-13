package chat.common.data;

import java.util.Objects;

/**
 * Client command data.
 */
public class CommandData {
    public String commandName;
    public String sender;
    public String message;
    public String receiver;

    public CommandData() {
    }

    public CommandData(String commandName, String sender, String message, String receiver) {
        this.commandName = commandName;
        this.sender = sender;
        this.message = message;
        this.receiver = receiver;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandData that = (CommandData) o;
        return Objects.equals(commandName, that.commandName) &&
                Objects.equals(sender, that.sender) &&
                Objects.equals(message, that.message) &&
                Objects.equals(receiver, that.receiver);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandName, sender, message, receiver);
    }

    @Override
    public String toString() {
        return "CommandData{" +
                "commandName='" + commandName + '\'' +
                ", sender='" + sender + '\'' +
                ", message='" + message + '\'' +
                ", receiver='" + receiver + '\'' +
                '}';
    }
}