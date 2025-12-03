import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws Exception {
        // TODO: Uncomment the code below to pass the first stage
        Scanner scanner = new Scanner(System.in);
        ArrayList<String> commands = new ArrayList<>();
        commands.add("echo");
        commands.add("exit");
        commands.add("type");

        while (true) {
            System.out.print("$ ");
            String command = scanner.nextLine();
            List<String> parts = List.of(command.split(" "));
            if (getCommand(parts).equals("exit")){
                break;
            }
            if (getCommand(parts).equals("echo")){
                System.out.println(command.substring(command.indexOf(" ") + 1));
                continue;
            }
            if (getCommand(parts).equals("type")){
                if (commands.contains(parts.get(1))){
                    System.out.println(parts.get(1) + " is a shell builtin");
                    continue;
                } else {
                    StringBuilder builder = new StringBuilder(parts.get(1));
                    builder.append(": not found ");
                    System.out.println(builder.toString());
                    continue;
                }

            }


            StringBuilder builder = new StringBuilder(command);
            builder.append(": command not found ");
            System.out.println(builder.toString());
        }



    }
    public static String getCommand(List<String> command){
        return command.get(0);
    }
}
