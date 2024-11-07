package ui;
import java.util.Scanner;

public class Client {
    private boolean LOGGED_IN;
    private Scanner scanner;
    private String user;

    public Client(Scanner scanner) {
        LOGGED_IN = false;
        this.scanner = scanner;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Client client = new Client(scanner);
            client.printPreLoginMenu();
            client.preLoginMenu();
            client.printPostLoginMenu();
            client.postLoginMenu();
        }
    }

    private void printPreLoginMenu() {
        System.out.printf("%nWelcome to CS240 Chess! Enter a number to proceed:%n");
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("3: Help");
        System.out.printf("4: Quit%n>>> ");
    }

    private void preLoginMenu() {
        while (!LOGGED_IN) {
            String line = scanner.nextLine().trim();
            System.out.println();

            switch (line) {
                case ("1"):
                    register();
                    break;
                case ("2"):
                    login();
                    break;
                case ("3"):
                    help();
                    break;
                case ("4"):
                    System.exit(0);
                    break;
                default:
                    printPreLoginMenu();
                    break;
            }
        }
    }

    private void register() {
        System.out.printf("Choose a username: ");
        user = scanner.nextLine().trim();
        System.out.printf("Choose a password: ");
        String password = scanner.nextLine().trim();
        System.out.printf("Enter your email: ");
        String email = scanner.nextLine().trim();
        LOGGED_IN = true;
    }

    private void login() {
        System.out.printf("Username: ");
        user = scanner.nextLine().trim();
        System.out.printf("Password: ");
        String password = scanner.nextLine().trim();
        LOGGED_IN = true;
    }

    private void help() {
        System.out.println("1: Register an account with <USERNAME>, <PASSWORD>, <EMAIL>");
        System.out.println("2: Login to an existing account with <USERNAME>, <PASSWORD>");
        System.out.println("3: Display this message again");
        System.out.printf("4: Exit the application%n>>> ");
    }

    private void printPostLoginMenu() {
        System.out.printf("%nWelcome, %s. Enter a number to proceed:%n", user);
        System.out.println("1: Help");
        System.out.println("2: Logout");
        System.out.println("3: Create Game");
        System.out.println("4: List Games");
        System.out.println("5: Play Game");
        System.out.printf("6: Observe Game%n>>> ");
    }

    private void postLoginMenu() {
        String line = scanner.nextLine().trim();
        System.out.println();

        switch (line) {
            case ("1"):
                loginHelp();
                postLoginMenu();
                break;
            case ("2"):
                logout();
                printPreLoginMenu();
                preLoginMenu();
                break;
            case ("3"):
                createGame();
                break;
            case ("4"):
                listGames();
                break;
            case ("5"):
                playGame();
                break;
            case ("6"):
                observeGame();
                break;
            default:
                printPostLoginMenu();
                break;
        }
    }

    private void loginHelp() {
        System.out.println("1: Display this message again");
        System.out.println("2: Logout and return to the start menu");
        System.out.println("3: Create a new chess game with <GAME_NAME>");
        System.out.println("4: List all games on the server");
        System.out.println(
                "5: Play a pre-existing game of chess with <GAME_ID> <TEAM_COLOR>[BLACK|WHITE]");
        System.out.printf("6: Observe a chess game with <GAME_ID>%n>>> ");
    }

    private void logout() {
        user = null;
        LOGGED_IN = false;
    }

    private void createGame() {
        System.out.printf("Game Name: ");
        String gameName = scanner.nextLine().trim();
    }

    private void listGames() {}

    private void playGame() {
        System.out.printf("Game ID: ");
        String gameID = scanner.nextLine().trim();
        System.out.printf("Team Color: ");
        String teamColor = scanner.nextLine().trim();
    }

    private void observeGame() {
        System.out.printf("Game ID: ");
        String gameID = scanner.nextLine().trim();
    }
}

