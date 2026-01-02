package commands;

import service.CommandExecutor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class EchoCommand implements Command {
    private String name;
    public EchoCommand() {
        name = "echo";
    }
    @Override
    public void execute(CommandExecutor executor, List<String> args){
        StringBuilder string = new StringBuilder("");
        StringBuilder directory = new StringBuilder("");
        boolean writingToDirectory = false;
        for (int i = 1; i < args.size(); i++){
            if (args.get(i).matches(">") || args.get(i).matches("1>")){
                writingToDirectory = true;
                continue;
            }
            if (writingToDirectory){
                directory.append(args.get(i));
                continue;
            }

            string.append(args.get(i));
            string.append(" ");
        }
        if (writingToDirectory){
            Path direct = Paths.get(directory.toString());
            try {
                Files.write(direct, string.toString().getBytes());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return;
        }
        System.out.println(string);

    }

    @Override
    public String getName(){
        return this.name;
    }
}
