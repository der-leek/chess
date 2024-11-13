package server;

import serializer.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public class ServerFacade {

    private HTTP http;
    private Serializer serializer = new Serializer();

    public ServerFacade(int port) {
        http = new HTTP(port);
    }

    public Map<String, String> clear() {
        try {
            return http.delete("db");
        } catch (IOException | URISyntaxException ex) {
            return null;
        }
    }

    public Map<String, String> register(String user, String password, String email) {
        var body = Map.of("username", user, "password", password, "email", email);

        try {
            return http.post("user", serializer.toJson(body));
        } catch (IOException | URISyntaxException ex) {
            return null;
        }
    }
}
