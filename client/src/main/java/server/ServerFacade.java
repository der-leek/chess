package server;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import chess.ChessGame;
import serializer.Serializer;

public class ServerFacade {

    private HTTP http;
    private Serializer serializer = new Serializer();

    public ServerFacade(int port) {
        http = new HTTP(port);
    }

    public Map<String, String> clear() {
        try {
            return http.delete("db", "");
        } catch (IOException | URISyntaxException ex) {
            return null;
        }
    }

    public Map<String, String> register(String user, String password, String email) {
        var body = Map.of("username", user, "password", password, "email", email);

        try {
            return http.post("user", serializer.toJson(body), "");
        } catch (IOException | URISyntaxException ex) {
            return null;
        }
    }

    public Map<String, String> login(String user, String password) {
        var body = Map.of("username", user, "password", password);

        try {
            return http.post("session", serializer.toJson(body), "");
        } catch (IOException | URISyntaxException ex) {
            return null;
        }
    }

    public Map<String, String> logout(String authToken) {
        try {
            return http.delete("session", authToken);
        } catch (IOException | URISyntaxException ex) {
            return null;
        }
    }

    public Map<String, String> createGame(String gameName, String authToken) {
        var body = Map.of("gameName", gameName);

        try {
            return http.post("game", serializer.toJson(body), authToken);
        } catch (IOException | URISyntaxException ex) {
            return null;
        }
    }

    public Map<String, String> listGames(String authToken) {
        try {
            return http.get("game", authToken);
        } catch (IOException | URISyntaxException ex) {
            return null;
        }
    }

    public Map<String, String> joinGame(Integer gameID, ChessGame.TeamColor playerColor,
            String authToken) {
        var body = Map.of("playerColor", playerColor, "gameID", gameID);

        try {
            return http.put("game", serializer.toJson(body), authToken);
        } catch (IOException | URISyntaxException ex) {
            return null;
        }
    }

}
