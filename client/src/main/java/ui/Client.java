package ui;

import server.*;
import java.util.Map;
import java.util.Scanner;
import chess.ChessBoard;
import serializer.*;

public class Client {
    private String user;
    private boolean LOGGED_IN;
    private String authToken;
    private final Scanner scanner;
    private final ServerFacade serverFacade;
    private final BoardRenderer boardRenderer;
    private final Serializer serializer = new Serializer();

    public Client(Scanner scanner) {
        boardRenderer = new BoardRenderer(new ChessBoard());
        serverFacade = new ServerFacade(8080);
        this.scanner = scanner;
        LOGGED_IN = false;
        authToken = null;
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

        var response = serverFacade.register(user, password, email);

        if (response == null) {
            System.out.println("\nThere was an error registering. Try again.\n");
            register();
        }

        if (!response.get("statusCode").equals("200")) {
            System.out.println("\nInvalid username. Try another.\n");
            register();
        }

        var body = serializer.fromJson(response.get("body"), Map.class);
        authToken = (String) body.get("authToken");
        LOGGED_IN = true;
    }

    private void login() {

    }

    private void help() {
        System.out.println("1: Register an account with <USERNAME>, <PASSWORD>, <EMAIL@MAIL.COM>");
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
        authToken = null;
        LOGGED_IN = false;
    }

    private void createGame() {
        System.out.printf("Game Name: ");
        // String gameName = scanner.nextLine().trim();
    }

    private void listGames() {}

    private void playGame() {
        System.out.printf("Game ID: ");
        // String gameID = scanner.nextLine().trim();
        System.out.printf("Team Color: ");
        // String teamColor = scanner.nextLine().trim();
        boardRenderer.drawBoard(false);
        System.out.println();
        boardRenderer.drawBoard(true);
    }

    private void observeGame() {
        System.out.printf("Game ID: ");
        // String gameID = scanner.nextLine().trim();
        boardRenderer.drawBoard(false);
        System.out.println();
        boardRenderer.drawBoard(true);
    }
}
