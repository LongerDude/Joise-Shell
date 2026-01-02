package service;

import commands.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class CommandExecutor {
    private final Map<String, Command> builtInCommands = new HashMap<>();
    private final PathResolver pathResolver;
    private Path cwd;


    public CommandExecutor(PathResolver pathResolver) {
        this.pathResolver = pathResolver;
        cwd = Paths.get(".").toAbsolutePath().normalize();
        // Register built-in commands here
        registerCommand(new ExitCommand());
        registerCommand(new EchoCommand());
        registerCommand(new TypeCommand());
        registerCommand(new PwdCommand());
        registerCommand(new CdCommand());
    }

    public Path getCwd() {
        return this.cwd;
    }

    public void setCwd(Path path) {
        this.cwd = path;
    }

    private void registerCommand(Command command) {
        builtInCommands.put(command.getName(), command);
    }

    public static List<String> parse(String line) {
        List<String> commandAndArguments = new ArrayList<>();
        State currentState = State.DEFAULT;
        State previousState = State.DEFAULT;
        CharStream stream = new CharStream(line);
        StringBuilder currentWord = new StringBuilder();

        while (stream.hasNext()) {
            char c = stream.next();
            switch (currentState) {
                case DEFAULT:
                    if (c == '"') {
                        currentState = State.IN_DOUBLE_QUOTES;
                        previousState = State.DEFAULT;
                        break;
                    }
                    if (c == '\'') {
                        currentState = State.IN_SINGLE_QUOTES;
                        previousState = State.DEFAULT;
                        break;
                    }
                    if (c == '\\') {
                        currentState = State.ESCAPED;
                        previousState = State.DEFAULT;
                        break;
                    }
                    if (c == ' ') {
                        if (!currentWord.isEmpty()) {
                            commandAndArguments.add(currentWord.toString());
                            currentWord.setLength(0);
                        }
                        break;
                    }
                    currentWord.append(c);
                    break;
                case IN_DOUBLE_QUOTES:
                    if (c == '\"') {
                        currentState = State.DEFAULT;
                        break;
                    }
                    if (c == '\\') {
                        char nextChar = stream.peek();
                        if (nextChar == '"' || nextChar == '$' || nextChar == '`' || nextChar == '\\') {
                            currentState = State.ESCAPED;
                            previousState = State.IN_DOUBLE_QUOTES;
                            break;
                        }

                    }
                    currentWord.append(c);
                    break;
                case IN_SINGLE_QUOTES:
                    if (c == '\'') {
                        currentState = State.DEFAULT;
                        break;
                    }
                    currentWord.append(c);
                    break;
                case ESCAPED:
                    currentWord.append(c);
                    currentState = previousState;
                    break;
            }


        }
        commandAndArguments.add(currentWord.toString());

        return commandAndArguments;


    }

    public boolean executeCommand(String commandLine) {
        List<String> commandAndArguments = parse(commandLine);
        String commandName = commandAndArguments.getFirst();
        if (isBuiltInCommand(commandName, commandAndArguments)) return true;
        Optional<Path> executablePath = pathResolver.findExecutable(commandName);

        if (executablePath.isEmpty()) {
            System.out.println(commandName + ": command not found");
            return true;
        }

        List<String> commandAndArgs = new ArrayList<>();
        commandAndArgs.add(executablePath.get().getFileName().toString());
        Path file = getPath(commandAndArguments, commandAndArgs);
        processor(commandAndArgs, file);
        return true;
    }

    private static Path getPath(List<String> commandAndArguments, List<String> commandAndArgs) {
        if (commandAndArguments.size() <= 1) {
            return null;
        }

        if (commandAndArguments.contains(">")) {
            commandAndArgs.addAll(commandAndArguments.subList(1, commandAndArguments.indexOf(">"))); // split string into elements
            return Paths.get(commandAndArguments.get(commandAndArguments.indexOf(">") + 1)); // isolationg the file path
        }


        return null;
    }

    public boolean isBuiltInCommand(String command, List<String> args) {
        if (!builtInCommands.containsKey(command)) {
            return false;
        }

        if (command.equals("type") && this.builtInCommands.containsKey(args.get(1))) { //for builtin commands
            System.out.println(args.get(1) + " is a shell builtin");
            return true;
        }

        builtInCommands.get(command).execute(this, args);
        return true;
    }

    public void processor(List<String> commandAndArgs, Path file) {
        ProcessBuilder builder = new ProcessBuilder(commandAndArgs);
        builder.directory(cwd.toFile());
        Process process;
        try {
            process = builder.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;


        while (true) {
            try {
                if ((line = reader.readLine()) == null) break; // break case
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (file == null) { // implicit redirection for writing standard output
                System.out.println(line);
                continue;
            }
            try {
                Files.writeString(file, line);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        while (true) {
            try {
                if ((line = errReader.readLine()) == null) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            System.out.println(line);
        }
    }
}