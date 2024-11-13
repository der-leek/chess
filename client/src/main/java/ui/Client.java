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
            System.out.print("Welcome to CS240 Chess!\n");
            while (true) {
                if (!client.LOGGED_IN) {
                    client.printPreLoginMenu();
                    client.runPreLoginMenu();
                } else {
                    client.runPostLoginMenu();
                }
            }
        }
    }

    private void printPreLoginMenu() {
        System.out.println("Enter a number to proceed:");
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("3: Help");
        System.out.print("4: Quit\n>>> ");
    }

    private void runPreLoginMenu() {
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
                System.out.print(">>> ");
                break;
        }
    }

    private void register() {
        while (!LOGGED_IN) {
            System.out.print("Choose a username: ");
            user = scanner.nextLine().trim();
            System.out.print("Choose a password: ");
            String password = scanner.nextLine().trim();
            System.out.print("Enter your email: ");
            String email = scanner.nextLine().trim();

            var response = serverFacade.register(user, password, email);

            if (response == null) {
                System.out.println("\nThere was an error registering. Try again.");
                continue;
            }

            if (!response.get("statusCode").equals("200")) { // TODO: refactor error handling
                System.out.println("\nInvalid username. Try another.\n");
                continue;
            }

            var body = serializer.fromJson(response.get("body"), Map.class);
            authToken = (String) body.get("authToken");
            LOGGED_IN = true;
        }
    }

    private void login() {
        while (!LOGGED_IN) {
            System.out.print("Username: ");
            user = scanner.nextLine().trim();
            System.out.print("Password: ");
            String password = scanner.nextLine().trim();

            var response = serverFacade.login(user, password);

            if (response == null) {
                System.out.println("\nThere was an error logging in. Try again.\n");
                continue;
            }

            if (!response.get("statusCode").equals("200")) { // TODO: refactor error handling
                System.out.println("\nInvalid credentials.\n");
                continue;
            }

            var body = serializer.fromJson(response.get("body"), Map.class);
            authToken = (String) body.get("authToken");
            LOGGED_IN = true;
        }
    }

    private void help() {
        System.out.println("1: Register an account with <USERNAME>, <PASSWORD>, <EMAIL@MAIL.COM>");
        System.out.println("2: Login to an existing account with <USERNAME>, <PASSWORD>");
        System.out.println("3: Display this message again");
        System.out.print("4: Exit the application\n>>> ");
    }

    private void printPostLoginMenu() {
        System.out.printf("%nWelcome, %s. Enter a number to proceed:%n", user);
        System.out.println("1: Help");
        System.out.println("2: Logout");
        System.out.println("3: Create Game");
        System.out.println("4: Play Game");
        System.out.println("5: List Games");
        System.out.print("6: Observe Game\n>>> ");
    }

    private void runPostLoginMenu() {
        printPostLoginMenu();
        String line = scanner.nextLine().trim();
        System.out.println();

        switch (line) {
            case ("1"):
                loginHelp();
                break;
            case ("2"):
                logout();
                break;
            case ("3"):
                createGame();
                break;
            case ("4"):
                playGame();
                break;
            case ("5"):
                listGames();
                break;
            case ("6"):
                observeGame();
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
        System.out.print("6: Observe a chess game with <GAME_ID>\n>>> ");
    }

    private void logout() {
        if (authToken == null) {
            return;
        }

        var response = serverFacade.logout(authToken);
        while (response == null) {
            System.out.println("\nThere was an error logging out. Trying again...\n");
            response = serverFacade.logout(authToken);
        }

        user = null;
        authToken = null;
        LOGGED_IN = false;
    }

    private void createGame() {
        System.out.print("Game Name: ");
        String gameName = scanner.nextLine().trim();
        var response = serverFacade.createGame(gameName, authToken);
        var statusCode = response.get("statusCode");

        if (response == null || !statusCode.equals("200")) {
            System.out.println("\nThere was an error creating that game. Please try again\n");
        }
    }

    private void listGames() {}

    private void playGame() {
        // System.out.print("Game ID: ");
        // String gameID = scanner.nextLine().trim();
        // System.out.print("Team Color: ");
        // String teamColor = scanner.nextLine().trim();
        boardRenderer.drawBoard(false);
        System.out.println();
        boardRenderer.drawBoard(true);
    }

    private void observeGame() {
        // System.out.print("Game ID: ");
        // String gameID = scanner.nextLine().trim();
        boardRenderer.drawBoard(false);
        System.out.println();
        boardRenderer.drawBoard(true);
    }
}
