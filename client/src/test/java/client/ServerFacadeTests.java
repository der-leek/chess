package client;

import model.*;
import serializer.*;
import org.junit.jupiter.api.*;
import server.Server;
import java.util.Map;
import java.util.ArrayList;
import server.ServerFacade;

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
}
