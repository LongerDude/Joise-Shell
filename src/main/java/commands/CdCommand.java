package commands;


import service.CommandExecutor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CdCommand implements Command {
    private String name;

    public CdCommand() {
        name = "cd";
    }

    public void execute(List<String> args) {
        System.setProperty("user.dir", args.get(1));
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void execute(List<String> args, CommandExecutor executor) {
        if (args.size() < 2) {
            System.err.println("cd: missing argument");
            return;
        }
        if (args.get(1).equals("~")){
            executor.setCwd(Paths.get(System.getProperty("user.dir")));
            return;
        }
        String targetPathString = args.get(1);
        Path currentDirectory = executor.getCwd();
        Path newPath = currentDirectory.resolve(targetPathString).normalize();
        if (Files.exists(newPath) && Files.isDirectory(newPath)) {
            executor.setCwd(newPath);
        } else {
            System.out.println("cd: " + targetPathString + ": No such file or directory");
        }
    }
}
