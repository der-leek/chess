package ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.reflect.TypeToken;
import chess.ChessBoard;
import chess.ChessGame;
import model.GameData;
import serializer.Serializer;
import server.ServerFacade;

public class Client {
    private String user;
    private boolean loggedIn;
    private String authToken;
    private Map<Integer, Integer> dbGames;
    private final Scanner scanner;
    private final ServerFacade serverFacade;
    private final BoardRenderer boardRenderer;
    private final Serializer serializer = new Serializer();


    public Client(Scanner scanner) {
        boardRenderer = new BoardRenderer(new ChessBoard());
        serverFacade = new ServerFacade(8080);
        dbGames = new HashMap<>();
        this.scanner = scanner;
        loggedIn = false;
        authToken = null;
    }

    public static void main(String[] args) {
        System.out.println();
        System.out.print(EscapeSequences.SET_BOLD_ITALIC);
        System.out.println("Welcome to CS240 Chess!");
        System.out.print(EscapeSequences.RESET_BOLD_ITALIC);

        try (Scanner scanner = new Scanner(System.in)) {
            Client client = new Client(scanner);
            while (true) {
                if (!client.loggedIn) {
                    System.out.println();
                    client.runPreLoginMenu();
                } else {
                    System.out.println();
                    client.runPostLoginMenu();
                }
            }
        }
    }

    private void printBoldItalic(String message) {
        System.out.print(EscapeSequences.SET_BOLD_ITALIC);
        System.out.println(message);
        System.out.print(EscapeSequences.RESET_BOLD_ITALIC);
    }

    private void printPreLoginMenu() {
        System.out.println("Enter a number to proceed:");
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("3: Help");
        System.out.print("4: Quit\n>>> ");
    }

    private void runPreLoginMenu() {
        printPreLoginMenu();
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
            case ("clear"):
                clearDB();
                break;
            default:
                break;
        }
    }

    private void clearDB() {
        var result = serverFacade.clear();
        if (result == null) {
            printBoldItalic("Remember to start the server!");
            return;
        }

        if (result.get("statusCode").equals("200")) {
            printBoldItalic("DB cleared!");
        }
    }

    private void register() {
        chooseUsername();
        String password = chooseEmail();
        String email = choosePassword();

        var response = serverFacade.register(user, password, email);

        if (response == null) {
            printBoldItalic("There was an error registering. Please try again.");
            return;
        }

        if (!response.get("statusCode").equals("200")) {
            printBoldItalic("Invalid username. Please try another.");
            return;
        }

        getAuthToken(response);
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
        getUsername();
        String password = getPassword();
        var response = serverFacade.login(user, password);
        String tryAgain = "There was an error logging in. Please try again.";

        if (response == null) {
            printBoldItalic(tryAgain);
            return;
        }

        var statusCode = response.get("statusCode");
        if (statusCode.equals("500")) {
            printBoldItalic(tryAgain);
            return;
        }

        if (statusCode.equals("401")) {
            printBoldItalic("Invalid credentials. Please try again.");
            return;
        }

        getAuthToken(response);
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
        System.out.print(EscapeSequences.SET_BOLD_ITALIC);

        System.out.println("1: Register an account with <USERNAME>, <PASSWORD>, <EMAIL@MAIL.COM>");
        System.out.println("2: Login to an existing account with <USERNAME>, <PASSWORD>");
        System.out.println("3: Display this message again");
        System.out.println("4: Exit CS240 Chess");

        System.out.print(EscapeSequences.RESET_BOLD_ITALIC);
    }

    private void printPostLoginMenu() {
        System.out.printf("Welcome, %s. Enter a number to proceed:\n", user);
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
            case ("clear"):
                clearDB();
                loggedIn = false;
                break;
        }
    }

    private void loginHelp() {
        System.out.print(EscapeSequences.SET_BOLD_ITALIC);

        System.out.println("1: Display this message again");
        System.out.println("2: Logout and return to the start menu");
        System.out.println("3: Create a new chess game with <GAME_NAME>");
        System.out.println("4: List all games <GAME_ID> on the server");
        System.out.println(
                "5: Play a pre-existing game of chess with <GAME_ID> <TEAM_COLOR>[WHITE|BLACK]");
        System.out.println("6: Observe a chess game with <GAME_ID>");

        System.out.print(EscapeSequences.RESET_BOLD_ITALIC);
    }

    private void logout() {
        if (authToken == null) {
            return;
        }

        var response = serverFacade.logout(authToken);

        while (response == null) {
            printBoldItalic("There was an error logging out. Trying again...");
            response = serverFacade.logout(authToken);
        }

        if (!response.get("statusCode").equals("200")) {
            printBoldItalic("There was an error logging out. Try again.");
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
        String tryAgain = "An error occurred while creating that game. Please try again";

        if (response == null) {
            printBoldItalic(tryAgain);
        }

        var statusCode = response.get("statusCode");
        if (!statusCode.equals("200")) {
            printBoldItalic(tryAgain);
        }

        listGames();
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
        String tryAgain = "An error occurred while listing games. Please try again.";

        if (response == null) {
            printBoldItalic(tryAgain);
            return;
        }

        var statusCode = response.get("statusCode");
        if (!statusCode.equals("200")) {
            printBoldItalic(tryAgain);
            return;
        }

        printGames(response);
    }

    private void printGames(Map<String, String> response) {
        Type type = new TypeToken<Map<String, ArrayList<GameData>>>() {}.getType();
        Map<String, ArrayList<GameData>> body = serializer.fromJson(response.get("body"), type);

        ArrayList<GameData> games = body.get("games");

        if (games.isEmpty()) {
            printBoldItalic("There are no games. Start by creating one.");
            return;
        }

        for (int i = 0; i < games.size(); i++) {
            var game = games.get(i);
            dbGames.put(i + 1, game.gameID());
            System.out.printf("%d: %s\n", i + 1, game.gameName());
            System.out.printf(" White Player: %s\n", game.whiteUsername());
            System.out.printf(" Black Player: %s\n", game.blackUsername());
        }
    }

    private void playGame() {
        if (authToken == null) {
            return;
        }

        if (dbGames.isEmpty()) {
            printBoldItalic("There are no games. Start by creating one.");
            return;
        }

        Integer gameID = getGameID();
        ChessGame.TeamColor teamColor = getColor();

        var response = serverFacade.joinGame(gameID, teamColor, authToken);
        String tryAgain = "An error occurred while joining that game. Please try again.";

        if (response == null) {
            printBoldItalic(tryAgain);
            return;
        }

        var statusCode = response.get("statusCode");
        if (statusCode.equals("403")) {
            printBoldItalic("Another user has joined as that color already. Please try again.");
            return;
        } else if (!statusCode.equals("200")) {
            printBoldItalic(tryAgain);
            return;
        }

        renderBoard(gameID);
    }

    private Integer getGameID() {
        System.out.print("Game ID: ");
        String gameID;
        Integer id = null;

        while (id == null) {
            gameID = scanner.nextLine().trim();

            id = parseGameID(gameID);

            if (id == null) {
                System.out.print("Please enter a valid game ID: ");
            }
        }

        return id;
    }

    private Integer parseGameID(String gameID) {
        try {
            return dbGames.get(Integer.parseInt(gameID));
        } catch (NumberFormatException ex) {
            return null;
        }
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
            validWhite = color.toUpperCase().equals("WHITE");
            validBlack = color.toUpperCase().equals("BLACK");
        }

        return colorMap.get(color.toUpperCase());
    }

    private void observeGame() {
        if (authToken == null) {
            return;
        }

        if (dbGames.isEmpty()) {
            printBoldItalic("There are no games. Start by creating one.");
            return;
        }

        Integer gameID = getGameID();

        renderBoard(gameID);
    }

    private void renderBoard(Integer gameID) {
        boardRenderer.drawBoard(false);
        System.out.println();
        boardRenderer.drawBoard(true);
    }
}
