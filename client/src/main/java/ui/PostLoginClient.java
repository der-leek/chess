package ui;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import com.google.gson.reflect.TypeToken;
import chess.ChessGame;
import model.GameData;
import serializer.Serializer;
import server.ServerFacade;

public class PostLoginClient {

    private String user;
    private String authToken;
    private Map<Integer, Integer> dbGames;
    private ChessGame.TeamColor teamColor;
    private final Scanner scanner;
    private final ServerFacade serverFacade;
    private final GameplayClient gameClient;
    private final Serializer serializer = new Serializer();

    public PostLoginClient(Scanner scanner, ServerFacade sf, int port) throws Exception {
        this.scanner = scanner;
        this.serverFacade = sf;
        this.gameClient = new GameplayClient(scanner, sf, port);
        dbGames = new HashMap<>();
    }

    public Login runMenu(Login credentials) throws Exception {
        this.user = credentials.username();
        this.authToken = credentials.authToken();
        listGames(false);

        System.out.println();
        printMenu();
        String line = scanner.nextLine().trim();
        System.out.println();

        switch (line) {
            case "1" -> help();
            case "2" -> logout();
            case "3" -> createGame();
            case "4" -> listGames(true);
            case "5" -> playGame();
            case "6" -> observeGame();
        }

        return new Login(user, authToken);
    }

    private void printMenu() {
        System.out.printf("Welcome, %s\n", user);
        System.out.println("1: Help");
        System.out.println("2: Logout");
        System.out.println("3: Create Game");
        System.out.println("4: List Games");
        System.out.println("5: Play Game");
        System.out.print("6: Observe Game\n>>> ");
    }

    private void help() {
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
        assertAuthTokenNotNull();
        var response = serverFacade.logout(authToken);

        if (response == null || !response.get("statusCode").equals("200")) {
            throw new RuntimeException("There was an error logging out. Try again.");
        }

        user = null;
        authToken = null;
    }

    private void assertAuthTokenNotNull() {
        if (authToken == null) {
            throw new RuntimeException();
        }
    }

    private void createGame() {
        assertAuthTokenNotNull();

        String gameName = getGameName();
        var response = serverFacade.createGame(gameName, authToken);
        String tryAgain = "An error occurred while creating that game. Please try again";

        if (response == null) {
            throw new RuntimeException(tryAgain);
        }

        var statusCode = response.get("statusCode");
        if (!statusCode.equals("200")) {
            throw new RuntimeException(tryAgain);
        }

        listGames(true);
    }

    private String getGameName() {
        System.out.print("Game Name: ");
        String gameName = scanner.nextLine().trim();
        return gameName;
    }

    private void listGames(boolean shouldPrint) {
        assertAuthTokenNotNull();

        ArrayList<GameData> games = retrieveGames();
        if (games == null || games.isEmpty()) {
            if (shouldPrint) {
                throw new RuntimeException("There are no games. Start by creating one.");
            }
        }

        processGames(games, shouldPrint);
    }

    private ArrayList<GameData> retrieveGames() {
        var response = serverFacade.listGames(authToken);
        String tryAgain = "An error occurred while listing games. Please try again.";

        if (response == null) {
            throw new RuntimeException(tryAgain);
        }

        var statusCode = response.get("statusCode");
        if (!statusCode.equals("200")) {
            throw new RuntimeException(tryAgain);
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

    private void playGame() throws Exception {
        assertAuthTokenNotNull();
        assertDBGamesNotEmpty();
        listGames(false);

        Integer gameID = getGameID();
        getColor();

        var joinResponse = serverFacade.joinGame(gameID, teamColor, authToken);
        validateJoinResponse(joinResponse);

        gameClient.runGameplayMenu(gameID, teamColor, authToken);
    }

    private void assertDBGamesNotEmpty() {
        if (dbGames.isEmpty()) {
            throw new RuntimeException("There are no games. Start by creating one.");
        }
    }

    private void validateJoinResponse(Map<String, String> response) throws Exception {
        String tryAgain = "An error occurred while joining that game. Please try again.";

        if (response == null) {
            throw new RuntimeException(tryAgain);
        }

        var statusCode = response.get("statusCode");
        if (statusCode.equals("403")) {
            throw new RuntimeException(
                    "Another user has joined as that color already. Please try again.");
        } else if (!statusCode.equals("200")) {
            throw new RuntimeException(tryAgain);
        }
    }

    private void observeGame() {
        assertAuthTokenNotNull();
        assertDBGamesNotEmpty();

        gameClient.runObserveMenu(getGameID(), authToken);
    }

    private Integer getGameID() {
        System.out.print("Game ID: ");
        String gameID;
        Integer id;

        do {
            gameID = scanner.nextLine().trim();
            id = parseGameID(gameID);

            if (id == null) {
                System.out.print("Please enter a valid game ID: ");
            }
        } while (id == null);

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
}
