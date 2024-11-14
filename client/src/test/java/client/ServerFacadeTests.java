package client;

import model.*;
import serializer.*;
import org.junit.jupiter.api.*;
import server.Server;
import java.util.Map;
import chess.ChessGame;
import java.util.HashSet;
import java.util.ArrayList;
import server.ServerFacade;
import java.lang.reflect.Type;
import com.google.gson.reflect.TypeToken;

public class ServerFacadeTests {

    private String authToken;
    private static Server server;
    private static ServerFacade sf;
    private final Serializer serializer = new Serializer();
    private final String username = "der_leek";
    private final String password = "23Der!";
    private final String email = "der@mail.com";

    @BeforeAll
    public static void setup() {
        int port = 8080;
        server = new Server();
        sf = new ServerFacade(port);
        server.run(8080);

        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        sf.clear();
        server.stop();
    }

    @BeforeEach
    public void init() {
        sf.clear();
        var registerResult = sf.register(username, password, email);
        var body = serializer.fromJson(registerResult.get("body"), Map.class);
        authToken = (String) body.get("authToken");
        Assertions.assertNotNull(authToken);
    }

    @Test
    public void clearSuccess() {
        Map<String, String> result = sf.clear();

        Assertions.assertNotNull(result);
        Assertions.assertEquals("200", result.get("statusCode"));
    }

    @Test
    public void registerSuccess() {
        Map<String, String> result = sf.register("new_username", password, email);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("200", result.get("statusCode"));

        var body = serializer.fromJson(result.get("body"), Map.class);
        Assertions.assertNotNull(body.get("authToken"));
    }

    @Test
    public void registerUsernameTaken() {
        Map<String, String> duplicate = sf.register(username, password, email);
        Assertions.assertNotNull(duplicate);
        Assertions.assertEquals("403", duplicate.get("statusCode"));
    }

    @Test
    public void logoutSuccess() {
        Map<String, String> logoutResult = sf.logout(authToken);
        Assertions.assertNotNull(logoutResult);
        Assertions.assertEquals("200", logoutResult.get("statusCode"));
    }

    @Test
    public void logoutBadAuth() {
        Map<String, String> logoutResult = sf.logout("badAuth");
        Assertions.assertNotNull(logoutResult);
        Assertions.assertEquals("401", logoutResult.get("statusCode"));
    }

    @Test
    public void logoutNullAuth() {
        Map<String, String> logoutResult = sf.logout(null);
        Assertions.assertNotNull(logoutResult);
        Assertions.assertEquals("401", logoutResult.get("statusCode"));
    }

    @Test
    public void loginSuccess() {
        sf.logout(authToken);

        Map<String, String> loginResult = sf.login(username, password);
        Assertions.assertNotNull(loginResult);
        Assertions.assertEquals("200", loginResult.get("statusCode"));
    }

    @Test
    public void loginInvalidUsername() {
        sf.logout(authToken);

        Map<String, String> loginResult = sf.login("bad_username", password);
        Assertions.assertNotNull(loginResult);
        Assertions.assertEquals("401", loginResult.get("statusCode"));
    }

    @Test
    public void loginInvalidPassword() {
        sf.logout(authToken);

        Map<String, String> loginResult = sf.login(username, "bad_password");
        Assertions.assertNotNull(loginResult);
        Assertions.assertEquals("401", loginResult.get("statusCode"));
    }

    @Test
    public void createGameSuccess() {
        Map<String, String> createResult = sf.createGame("new game", authToken);
        Assertions.assertNotNull(createResult);
        Assertions.assertEquals("200", createResult.get("statusCode"));

        var body = serializer.fromJson(createResult.get("body"), Map.class);
        var gameID = body.get("gameID");
        Assertions.assertInstanceOf(Double.class, gameID);
    }

    @Test
    public void createTwoGames() {
        Map<String, String> createResult = sf.createGame("new game", authToken);
        Assertions.assertNotNull(createResult);
        Assertions.assertEquals("200", createResult.get("statusCode"));

        Map<String, String> newCreateResult = sf.createGame("another game", authToken);
        Assertions.assertNotNull(newCreateResult);
        Assertions.assertEquals("200", newCreateResult.get("statusCode"));
    }

    @Test
    public void createGameBadAuth() {
        Map<String, String> createResult = sf.createGame("new game", "badAuth");
        Assertions.assertNotNull(createResult);
        Assertions.assertEquals("401", createResult.get("statusCode"));
    }

    @Test
    public void createGameBadGameName() {
        Map<String, String> createResult = sf.createGame("badName;", authToken);
        Assertions.assertNotNull(createResult);
        Assertions.assertEquals("500", createResult.get("statusCode"));
    }

    @Test
    public void listGamesBadAuth() {
        Map<String, String> listResult = sf.listGames("badAuth");
        Assertions.assertNotNull(listResult);
        Assertions.assertEquals("401", listResult.get("statusCode"));
    }

    @Test
    public void listNoGames() {
        Map<String, String> listResult = sf.listGames(authToken);
        Assertions.assertNotNull(listResult);
        Assertions.assertEquals("200", listResult.get("statusCode"));

        Type type = new TypeToken<Map<String, ArrayList<GameData>>>() {}.getType();
        Map<String, ArrayList<GameData>> body = serializer.fromJson(listResult.get("body"), type);
        ArrayList<GameData> games = body.get("games");

        Assertions.assertNotNull(games);
        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    public void listTwoGames() {
        HashSet<String> gameNames = new HashSet<>();
        HashSet<Integer> gameIDs = new HashSet<>();

        Map<String, String> createResult = sf.createGame("game1", authToken);
        var createBody = serializer.fromJson(createResult.get("body"), Map.class);
        gameIDs.add(((Double) createBody.get("gameID")).intValue());
        gameNames.add("game1");

        Map<String, String> newCreateResult = sf.createGame("game2", authToken);
        var newCreateBody = serializer.fromJson(newCreateResult.get("body"), Map.class);
        gameIDs.add(((Double) newCreateBody.get("gameID")).intValue());
        gameNames.add("game2");

        Map<String, String> listResult = sf.listGames(authToken);
        Assertions.assertNotNull(listResult);

        Type type = new TypeToken<Map<String, ArrayList<GameData>>>() {}.getType();
        Map<String, ArrayList<GameData>> body = serializer.fromJson(listResult.get("body"), type);

        ArrayList<GameData> games = body.get("games");
        Assertions.assertNotNull(games);
        Assertions.assertFalse(games.isEmpty());

        for (var game : games) {
            Assertions.assertNotNull(game);
            Assertions.assertTrue(gameNames.contains(game.gameName()));
            Assertions.assertTrue(gameIDs.contains(game.gameID()));
            Assertions.assertNull(game.whiteUsername());
            Assertions.assertNull(game.blackUsername());
            Assertions.assertNotNull(game.game());
        }
    }

    @Test
    public void joinGameSuccess() {
        Map<String, String> createResult = sf.createGame("newGame", authToken);
        var body = serializer.fromJson(createResult.get("body"), Map.class);
        Double gameID = (Double) body.get("gameID");

        Map<String, String> joinResult =
                sf.joinGame(Double.toString(gameID), ChessGame.TeamColor.WHITE, authToken);

        Assertions.assertNotNull(joinResult);
        Assertions.assertEquals("200", joinResult.get("statusCode"));
        // add tests to verify that the game exists in the database
    }
}
