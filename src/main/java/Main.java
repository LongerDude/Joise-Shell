import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> commands = new ArrayList<>();
        commands.add("echo");
        commands.add("exit");
        commands.add("type");

        while (true) {
            System.out.print("$ ");
            String command = scanner.nextLine();
            List<String> parts = List.of(command.split(" "));
            if (getCommand(parts).equals("exit")) {
                break;
            }
            if (getCommand(parts).equals("echo")) {
                System.out.println(command.substring(command.indexOf(" ") + 1));
                continue;
            }
            if (getCommand(parts).equals("type")) {
                if (commands.contains(parts.get(1))) {
                    System.out.println(parts.get(1) + " is a shell builtin");
                    continue;
                } else {
                    findInPath(parts.get(1));
                    continue;
                }
            }


            StringBuilder builder = new StringBuilder(command);
            builder.append(": command not found ");
            System.out.println(builder.toString());
        }


    }

    public static String getCommand(List<String> command) {
        return command.get(0);
    }

    public static void findInPath(String command) {
        String PATH = System.getenv("PATH");
        if (PATH != null) {
            String[] directories = PATH.split(File.pathSeparator);
            Optional<Path> opt = null;
            for (String directoryPath : directories) {
                Path path = Paths.get(directoryPath);
                if (Files.isDirectory(path)) {
                    try (Stream<Path> paths = Files.walk(path, 1)) {
                        opt = paths.filter(Files::isExecutable).filter(file -> file.toFile().getName().equals(command)).findAny();
                        if (opt.isPresent()) {
                            System.out.println(command +" is "+opt.get().getParent() + "/" + opt.get().getFileName());
                            return;
                        }

                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }

                }
            }
            System.out.println(command+": not found");

        }
    }

}

