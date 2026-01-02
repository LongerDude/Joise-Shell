package commands;

import service.CommandExecutor;

import java.nio.file.Path;
import java.util.List;

public class PwdCommand implements Command {
    private String name;
    public PwdCommand(){
        this.name = "pwd";
    }

    @Override
    public void execute(CommandExecutor executor, List<String> args){
        Path currentDirectory = executor.getCwd();
        System.out.println(currentDirectory);

    }

    @Override
    public String getName(){
        return this.name;
    }
}
