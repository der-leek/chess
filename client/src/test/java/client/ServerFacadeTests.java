package client;

import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;
import java.util.Map;
import serializer.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade sf;
    private final Serializer serializer = new Serializer();

    @BeforeAll
    public static void init() {
        int port = 8080;
        server = new Server();
        sf = new ServerFacade(port);
        server.run(8080);

        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    public void purge() {
        sf.clear();
    }

    @Test
    public void clearSuccess() {
        Map<String, String> result = sf.clear();

        Assertions.assertNotNull(result);
        Assertions.assertEquals("200", result.get("statusCode"));
    }

    @Test
    public void registerSuccess() {
        Map<String, String> result = sf.register("der_leek", "23Der!", "der@mail.com");

        Assertions.assertNotNull(result);
        Assertions.assertEquals("200", result.get("statusCode"));

        var body = serializer.fromJson(result.get("body"), Map.class);
        Assertions.assertNotNull(body.get("authToken"));
    }

    @Test
    public void registerUsernameTaken() {
        Map<String, String> result = sf.register("der_leek", "23Der!", "der@mail.com");
        Assertions.assertNotNull(result);
        Assertions.assertEquals("200", result.get("statusCode"));

        Map<String, String> duplicate = sf.register("der_leek", "23Der!", "der@mail.com");
        Assertions.assertNotNull(duplicate);
        Assertions.assertEquals("403", duplicate.get("statusCode"));
    }

    @Test
    public void logoutSuccess() {
        Map<String, String> registerResult = sf.register("der_leek", "23Der!", "der@mail.com");
        Assertions.assertNotNull(registerResult);
        Assertions.assertEquals("200", registerResult.get("statusCode"));

        var body = serializer.fromJson(registerResult.get("body"), Map.class);
        var authToken = (String) body.get("authToken");

        Map<String, String> logoutResult = sf.logout(authToken);
        Assertions.assertNotNull(logoutResult);
        Assertions.assertEquals("200", logoutResult.get("statusCode"));
    }

    @Test
    public void logoutBadAuth() {
        Map<String, String> registerResult = sf.register("der_leek", "23Der!", "der@mail.com");
        Assertions.assertNotNull(registerResult);
        Assertions.assertEquals("200", registerResult.get("statusCode"));

        Map<String, String> logoutResult = sf.logout("badAuth");
        Assertions.assertNotNull(logoutResult);
        Assertions.assertEquals("401", logoutResult.get("statusCode"));
    }

    @Test
    public void logoutNullAuth() {
        Map<String, String> registerResult = sf.register("der_leek", "23Der!", "der@mail.com");
        Assertions.assertNotNull(registerResult);
        Assertions.assertEquals("200", registerResult.get("statusCode"));

        Map<String, String> logoutResult = sf.logout(null);
        Assertions.assertNotNull(logoutResult);
        Assertions.assertEquals("401", logoutResult.get("statusCode"));
    }
}
