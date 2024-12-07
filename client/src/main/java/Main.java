import ui.*;
import java.util.Scanner;
import server.ServerFacade;

public class Main {
    public static void main(String[] args) throws Exception {
        System.out.print(EscapeSequences.SET_BOLD_ITALIC);
        System.out.println("Welcome to CS240 Chess!");
        System.out.print(EscapeSequences.RESET_BOLD_ITALIC);

        try (Scanner scanner = new Scanner(System.in)) {
            runClient(scanner);
        }
    }

    private static void runClient(Scanner scanner) throws Exception {
        int port = 8080;
        var sf = new ServerFacade(port);

        var preClient = new PreLoginClient(scanner, sf);
        var postClient = new PostLoginClient(scanner, sf, port);

        Login auth = new Login(null, null);
        while (true) {
            if (auth.username() == null) {
                
                auth = preClient.runMenu();
            } else {
                auth = postClient.runMenu(auth);
            }
        }
    }
}
