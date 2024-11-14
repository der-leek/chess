package ui;

import server.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.reflect.TypeToken;
import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import serializer.*;

public class Client {
    private String user;
    private boolean loggedIn;
    private String authToken;
    private final Scanner scanner;
    private final ServerFacade serverFacade;
    private final BoardRenderer boardRenderer;
    private final Serializer serializer = new Serializer();

    public Client(Scanner scanner) {
        boardRenderer = new BoardRenderer(new ChessBoard());
        serverFacade = new ServerFacade(8080);
        this.scanner = scanner;
        loggedIn = false;
        authToken = null;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Client client = new Client(scanner);
            System.out.print("Welcome to CS240 Chess!\n");
            while (true) {
                if (!client.loggedIn) {
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
        while (!loggedIn) {
            chooseUsername();
            String password = chooseEmail();
            String email = choosePassword();

            var response = serverFacade.register(user, password, email);

            if (response == null) {
                System.out.println("\nThere was an error registering. Please try again.");
                continue;
            }

            if (!response.get("statusCode").equals("200")) {
                System.out.println("\nInvalid username. Please try another.\n");
                continue;
            }

            getAuthToken(response);
        }
    }

    private void getAuthToken(Map<String, String> response) {
        var body = serializer.fromJson(response.get("body"), Map.class);
        authToken = (String) body.get("authToken");
        loggedIn = true;
    }

    private String choosePassword() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();
        return email;
    }

    private String chooseEmail() {
        System.out.print("Choose a password: ");
        String password = scanner.nextLine().trim();
        return password;
    }

    private void chooseUsername() {
        System.out.print("Choose a username: ");
        user = scanner.nextLine().trim();
    }

    private void login() {
        while (!loggedIn) {
            getUsername();
            String password = getPassword();
            var response = serverFacade.login(user, password);
            String tryAgain = "\nThere was an error logging in. Please try again.\n";

            if (response == null) {
                System.out.println(tryAgain);
                continue;
            }

            var statusCode = response.get("statusCode");
            if (statusCode.equals("500")) {
                System.out.println(tryAgain);
                continue;
            }

            if (statusCode.equals("401")) {
                System.out.println("\nInvalid credentials. Please try again.\n");
                continue;
            }

            getAuthToken(response);
        }
    }

    private void getUsername() {
        System.out.print("Username: ");
        user = scanner.nextLine().trim();
    }

    private String getPassword() {
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        return password;
    }

    private void help() {
        System.out.println(EscapeSequences.SET_TEXT_BOLD);
        System.out.println(EscapeSequences.SET_TEXT_ITALIC);

        System.out.println("1: Register an account with <USERNAME>, <PASSWORD>, <EMAIL@MAIL.COM>");
        System.out.println("2: Login to an existing account with <USERNAME>, <PASSWORD>");
        System.out.println("3: Display this message again");
        System.out.println("4: Exit CS240 Chess");

        System.out.println(EscapeSequences.RESET_TEXT_ITALIC);
        System.out.println(EscapeSequences.RESET_TEXT_BOLD_FAINT);
    }

    private void printPostLoginMenu() {
        System.out.printf("%nWelcome, %s. Enter a number to proceed:%n", user);
        System.out.println("1: Help");
        System.out.println("2: Logout");
        System.out.println("3: Create Game");
        System.out.println("4: List Games");
        System.out.println("5: Play Game");
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
                listGames();
                break;
            case ("5"):
                playGame();
                break;
            case ("6"):
                observeGame();
                break;
        }
    }

    private void loginHelp() {
        System.out.println(EscapeSequences.SET_TEXT_BOLD);
        System.out.println(EscapeSequences.SET_TEXT_ITALIC);

        System.out.println("1: Display this message again");
        System.out.println("2: Logout and return to the start menu");
        System.out.println("3: Create a new chess game with <GAME_NAME>");
        System.out.println("4: List all games <GAME_ID> on the server");
        System.out.println(
                "5: Play a pre-existing game of chess with <GAME_ID> <TEAM_COLOR>[WHITE|BLACK]");
        System.out.println("6: Observe a chess game with <GAME_ID>");

        System.out.println(EscapeSequences.RESET_TEXT_ITALIC);
        System.out.println(EscapeSequences.RESET_TEXT_BOLD_FAINT);
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

        if (!response.get("statusCode").equals("200")) {
            System.out.println("\nThere was an error logging out. Please try again...\n");
            return;
        }

        user = null;
        authToken = null;
        loggedIn = false;
    }

    private void createGame() {
        if (authToken == null) {
            return;
        }

        String gameName = getGameName();
        var response = serverFacade.createGame(gameName, authToken);
        String tryAgain = "\nAn error occurred while creating that game. Please try again\n";

        if (response == null) {
            System.out.println(tryAgain);
        }

        var statusCode = response.get("statusCode");
        if (!statusCode.equals("200")) {
            System.out.println(tryAgain);
        }
    }

    private String getGameName() {
        System.out.print("Game Name: ");
        String gameName = scanner.nextLine().trim();
        return gameName;
    }

    private void listGames() {
        if (authToken == null) {
            return;
        }

        var response = serverFacade.listGames(authToken);
        String tryAgain = "\nAn error occurred while listing games. Please try again\n";

        if (response == null) {
            System.out.println(tryAgain);
        }

        var statusCode = response.get("statusCode");
        if (!statusCode.equals("200")) {
            System.out.println(tryAgain);
        }

        printGames(response);
    }

    private void printGames(Map<String, String> response) {
        Type type = new TypeToken<Map<String, ArrayList<GameData>>>() {}.getType();
        Map<String, ArrayList<GameData>> body = serializer.fromJson(response.get("body"), type);

        ArrayList<GameData> games = body.get("games");

        if (games.isEmpty()) {
            System.out.println("There are no games. Start by creating one.");
            return;
        }

        for (int i = 0; i < games.size(); i++) {
            var game = games.get(i);
            System.out.printf("%d: %s\n", i + 1, game.gameName());
            System.out.printf(" White Player: %s\n", game.whiteUsername());
            System.out.printf(" Black Player: %s\n", game.blackUsername());
        }
    }

    private void playGame() {
        if (authToken == null) {
            return;
        }

        String gameID = getGameID();
        var teamColor = getColor();

        var response = serverFacade.joinGame(gameID, teamColor, authToken);
        String tryAgain = "\nAn error while joining that game. Please try again\n";

        if (response == null) {
            System.out.println(tryAgain);
        }

        var statusCode = response.get("statusCode");
        if (!statusCode.equals("200")) {
            System.out.println(tryAgain);
        }

        renderBoard();
    }

    private String getGameID() {
        System.out.print("Game ID: ");
        String gameID = scanner.nextLine().trim();
        return gameID;
    }

    private ChessGame.TeamColor getColor() {
        System.out.print("Team Color: ");
        String color = scanner.nextLine().trim();

        boolean validWhite = color.toUpperCase().equals("WHITE");
        boolean validBlack = color.toUpperCase().equals("BLACK");
        var colorMap =
                Map.of("WHITE", ChessGame.TeamColor.WHITE, "BLACK", ChessGame.TeamColor.BLACK);

        while (!validWhite && !validBlack) {
            System.out.print("Team Color (must be 'white' or 'black'): ");
            color = scanner.nextLine().trim();
        }

        return colorMap.get(color.toUpperCase());
    }

    private void observeGame() {
        if (authToken == null) {
            return;
        }

        // System.out.print("Game ID: ");
        // String gameID = scanner.nextLine().trim();

        renderBoard();
    }

    private void renderBoard() {
        boardRenderer.drawBoard(false);
        System.out.println();
        boardRenderer.drawBoard(true);
    }
}
