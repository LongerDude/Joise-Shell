package commands;

import service.CommandExecutor;

import java.util.List;

public interface Command {
    // A command needs to execute based on the command parts (arguments)
    void execute(CommandExecutor executor, List<String> args);
    String getName();
}
