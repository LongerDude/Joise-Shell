import commands.PathResolver;
import service.CommandExecutor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        PathResolver resolver = new PathResolver();
        CommandExecutor executor = new CommandExecutor(resolver);


        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("$ ");
            String command = scanner.nextLine();

            // The executor handles all the complexity
            boolean shouldContinue = executor.executeCommand(command);
            if (!shouldContinue) {
                break;
            }
        }
    }
}