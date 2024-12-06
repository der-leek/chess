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
import server.ServerMessageObserver;
import server.WebSocketCommunicator;
import websocket.commands.*;
import websocket.messages.*;

public class Client implements ServerMessageObserver {
    public static void main(String[] args) throws Exception {
        System.out.println();
        System.out.print(EscapeSequences.SET_BOLD_ITALIC);
        System.out.println("Welcome to CS240 Chess!");
        System.out.print(EscapeSequences.RESET_BOLD_ITALIC);

        try (Scanner scanner = new Scanner(System.in)) {
            runClient(scanner);
        }
    }

    private static void runClient(Scanner scanner) throws Exception {
        Client client = new Client(scanner);
        while (true) {
            if (!client.loggedIn) {
                client.runPreLoginMenu();
            } else {
                client.runPostLoginMenu();
            }
        }
    }

    private String user;
    private String authToken;
    private boolean loggedIn;
    private ChessGame chessGame;
    private boolean gameInProgress;
    private ChessGame.TeamColor teamColor;
    private Map<Integer, Integer> dbGames;
    private final Scanner scanner;
    private final WebSocketCommunicator ws;
    private final ServerFacade serverFacade;
    private final BoardRenderer boardRenderer;
    private final Serializer serializer = new Serializer();

    public Client(Scanner scanner) throws Exception {
        int port = 8080;
        boardRenderer = new BoardRenderer();
        serverFacade = new ServerFacade(port);
        ws = new WebSocketCommunicator(port, this);

        dbGames = new HashMap<>();
        this.scanner = scanner;
        loggedIn = false;
        authToken = null;
    }

    @Override
    public void notify(ServerMessage message) {
        switch (message.getServerMessageType()) {
            case NOTIFICATION -> displayNotification((NotificationMessage) message);
            case ERROR -> displayError((ErrorMessage) message);
            case LOAD_GAME -> loadGame((LoadGameMessage) message);
        }
    }

    public void displayNotification(NotificationMessage message) {
        System.out.println();
        System.out.println();

        printBoldItalic(message.getMessage());

        System.out.println();
        System.out.print(">>> ");
    }

    public void displayError(ErrorMessage message) {
        System.out.println();
        System.out.println();

        printBoldItalic(message.getErrorMessage());

        System.out.println();
        System.out.print(">>> ");
    }

    public void loadGame(LoadGameMessage message) {
        chessGame = message.getGame();
        System.out.println();

        if (chessGame == null) {
            printBoldItalic("There was an error retrieving the game");
            return;
        }

        if (teamColor == null) {
            renderBoard(chessGame.getBoard(), ChessGame.TeamColor.WHITE);
        } else {
            renderBoard(chessGame.getBoard(), teamColor);
        }

        System.out.print(">>> ");
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
        System.out.println();
        printPreLoginMenu();
        String line = scanner.nextLine().trim();
        System.out.println();

        switch (line) {
            case "1" -> register();
            case "2" -> login();
            case "3" -> help();
            case "4" -> System.exit(0);
            case "clear" -> clearDB();
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

        loggedIn = false;
        gameInProgress = false;
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
        listGames(false);
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
        System.out.printf("Welcome, %s\n", user);
        System.out.println("1: Help");
        System.out.println("2: Logout");
        System.out.println("3: Create Game");
        System.out.println("4: List Games");
        System.out.println("5: Play Game");
        System.out.print("6: Observe Game\n>>> ");
    }

    private void runPostLoginMenu() {
        System.out.println();
        printPostLoginMenu();
        String line = scanner.nextLine().trim();
        System.out.println();

        switch (line) {
            case "1" -> loginHelp();
            case "2" -> logout();
            case "3" -> createGame();
            case "4" -> listGames(true);
            case "5" -> playGame();
            case "6" -> observeGame();
            case "clear" -> clearDB();
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

        listGames(true);
    }

    private String getGameName() {
        System.out.print("Game Name: ");
        String gameName = scanner.nextLine().trim();
        return gameName;
    }

    private void listGames(boolean shouldPrint) {
        if (authToken == null) {
            return;
        }

        ArrayList<GameData> games = retrieveGames();
        if (games == null || games.isEmpty()) {
            printBoldItalic("There are no games. Start by creating one.");
        }

        processGames(games, shouldPrint);
    }

    private ArrayList<GameData> retrieveGames() {
        var response = serverFacade.listGames(authToken);
        String tryAgain = "An error occurred while listing games. Please try again.";

        if (response == null) {
            printBoldItalic(tryAgain);
            return null;
        }

        var statusCode = response.get("statusCode");
        if (!statusCode.equals("200")) {
            printBoldItalic(tryAgain);
            return null;
        }

        Type type = new TypeToken<Map<String, ArrayList<GameData>>>() {}.getType();
        Map<String, ArrayList<GameData>> body = serializer.fromJson(response.get("body"), type);

        return body.get("games");
    }

    private void processGames(ArrayList<GameData> games, boolean shouldPrint) {
        for (int i = 0; i < games.size(); i++) {
            var game = games.get(i);
            dbGames.put(i + 1, game.gameID());
            if (shouldPrint) {
                printGame(i, game);
            }
        }
    }

    private void printGame(int i, GameData game) {
        System.out.printf("%d: %s\n", i + 1, game.gameName());
        System.out.printf(" White Player: %s\n", game.whiteUsername());
        System.out.printf(" Black Player: %s\n", game.blackUsername());
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
        getColor();

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

        try {
            ws.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
        } catch (Exception ex) {
            printBoldItalic(ex.getMessage());
            return;
        }

        gameInProgress = true;
        runGameplayMenu(gameID, teamColor);
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

    private void getColor() {
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

        teamColor = colorMap.get(color.toUpperCase());
    }

    private void renderBoard(ChessBoard board, ChessGame.TeamColor teamColor) {
        boolean reversed = (teamColor == ChessGame.TeamColor.WHITE ? false : true);

        System.out.println();
        boardRenderer.drawBoard(board, reversed);
        System.out.println();
    }

    private void runGameplayMenu(int gameID, ChessGame.TeamColor teamColor) {
        while (gameInProgress) {
            System.out.println();
            printGameplayMenu();
            String line = scanner.nextLine().trim();
            System.out.println();

            switch (line) {
                case "1" -> printGameplayHelp();
                case "2" -> renderBoard(chessGame.getBoard(), teamColor);
                case "3" -> leaveGame(gameID);
                case "4" -> makeMove();
                case "5" -> resign(gameID);
                case "6" -> highlightMoves();
                case "clear" -> clearDB();
            }
        }
    }

    private void printGameplayMenu() {
        System.out.println("1: Help");
        System.out.println("2: Redraw Chess Board");
        System.out.println("3: Leave Game");
        System.out.println("4: Make Move");
        System.out.println("5: Resign");
        System.out.print("6: Highlight Legal Moves\n>>> ");
    }

    private void printGameplayHelp() {
        System.out.print(EscapeSequences.SET_BOLD_ITALIC);

        System.out.println("1: Display this message again");
        System.out.println("2: Redraw the current state of the chess board");
        System.out.println("3: Leave the game and return to the login menu");
        System.out.println("4: Make a move from <START_POSITION> to <END_POSITION> (e.g. b1 c5)");
        System.out.println("5: End the game by resigning");
        System.out.println("6: Show the legal moves for a given <POSITION> (e.g. h8)");

        System.out.print(EscapeSequences.RESET_BOLD_ITALIC);
    }

    private void leaveGame(int gameID) {
        try {
            ws.send(new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID));
        } catch (Exception ex) {
            printBoldItalic(ex.getMessage());
            return;
        }

        gameInProgress = false;
    }

    private void makeMove() {
        // convert moves into ChessPositions
        // send MAKE MOVE request via websocket
    }

    private void resign(int gameID) {
        // System.out.print("Are you sure you want to resign? (y/n) ");
        // String line = scanner.nextLine().trim();
        // System.out.println();

        // send RESIGN request via websocket
    }

    private void highlightMoves() {
        // request board via websocket
        // redraw board with highlighted moves (modify BoardRenderer)
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

        try {
            ws.send(new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID));
        } catch (Exception ex) {
            printBoldItalic(ex.getMessage());
            return;
        }

        gameInProgress = true;
        runObserveMenu(gameID);
    }

    private void runObserveMenu(int gameID) {
        while (gameInProgress) {
            System.out.println();
            printObserveMenu();
            String line = scanner.nextLine().trim();
            System.out.println();

            switch (line) {
                case "1" -> printObserveHelp();
                case "2" -> renderBoard(chessGame.getBoard(), ChessGame.TeamColor.WHITE);
                case "3" -> leaveGame(gameID);
                case "4" -> highlightMoves();
                case "clear" -> clearDB();
            }
        }
    }

    private void printObserveMenu() {
        System.out.println("1: Help");
        System.out.println("2: Redraw Chess Board");
        System.out.println("3: Leave Game");
        System.out.print("4: Highlight Legal Moves\n>>> ");
    }

    private void printObserveHelp() {
        System.out.print(EscapeSequences.SET_BOLD_ITALIC);

        System.out.println("1: Display this message again");
        System.out.println("2: Redraw the current state of the chess board");
        System.out.println("3: Leave the game and return to the login menu");
        System.out.println("4: Show the legal moves for a given <POSITION> (e.g. h8)");

        System.out.print(EscapeSequences.RESET_BOLD_ITALIC);
    }
}
