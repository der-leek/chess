package server;

import chess.*;
import spark.*;
import service.*;
import requests.*;
import responses.*;
import dataaccess.*;
import model.GameData;
import websocket.commands.*;
import websocket.messages.*;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.io.IOException;
import serializer.Serializer;
import com.google.gson.JsonSyntaxException;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

@WebSocket
public class Server {
    private Map<Integer, HashSet<Session>> sessions;
    private Map<Session, HashSet<Integer>> reverseSessions;
    private final DataAccess dataAccess;
    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;
    private final Serializer serializer = new Serializer();
    private final Map<Integer, Character> columns =
            Map.of(1, 'a', 2, 'b', 3, 'c', 4, 'd', 5, 'e', 6, 'f', 7, 'g', 8, 'h');

    public Server() {
        try {
            dataAccess = new MySqlDataAccess();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        clearService = new ClearService(dataAccess);
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
        sessions = new HashMap<>();
        reverseSessions = new HashMap<>();
    }

    public Integer run(int desiredPort) {
        Spark.port(desiredPort);
        Spark.staticFiles.location("web");

        createRoutes();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    @OnWebSocketClose
    public void cleanWebSocketSessions(Session session, int exitCode, String reason) {
        var games = reverseSessions.get(session);
        if (games == null) {
            return;
        }

        for (int gameID : games) {
            var currentSessionsSet = sessions.get(gameID);
            if (currentSessionsSet == null) {
                continue;
            }

            currentSessionsSet.remove(session);
            if (currentSessionsSet.isEmpty()) {
                sessions.remove(gameID);
            }
        }

        reverseSessions.remove(session);
        System.out.printf("%s: %s\n", session.getRemote().toString(), reason);
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            var command = serializer.fromJson(message, UserGameCommand.class);
            var game = findGame(command.getAuthToken(), command.getGameID());
            String user = userService.getUsername(command.getAuthToken());
            ChessGame.TeamColor teamColor = getTeamColor(user, game);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, command, user, teamColor, game);
                case MAKE_MOVE -> makeMove(session, command, user, teamColor, game);
                case LEAVE -> leaveGame(session, command, user, teamColor);
                case RESIGN -> resign(session, command, user, teamColor, game);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            sendServerMessage(session, new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private ChessGame.TeamColor getTeamColor(String user, GameData game) {
        ChessGame.TeamColor teamColor = null;
        if (user.equals(game.whiteUsername())) {
            teamColor = ChessGame.TeamColor.WHITE;
        } else if (user.equals(game.blackUsername())) {
            teamColor = ChessGame.TeamColor.BLACK;
        }
        return teamColor;
    }

    private GameData findGame(String authToken, int gameID)
            throws AuthorizationException, DataAccessException {
        var game = gameService.findGame(gameID, authToken);
        if (game == null) {
            sessions.remove(gameID);
            throw new NullPointerException("Invalid game ID");
        }
        return game;
    }

    private void sendServerMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(serializer.toJson(message));
    }

    private void connect(Session session, UserGameCommand command, String username,
            ChessGame.TeamColor teamColor, GameData game)
            throws AuthorizationException, DataAccessException, IOException {

        var currentSessions = addSessionToGame(session, command.getGameID());
        sendConnectMessages(session, currentSessions, command, username, teamColor, game);
    }

    private HashSet<Session> addSessionToGame(Session session, int gameID) {
        var currentSessions = sessions.get(gameID);
        var currentGames = reverseSessions.get(session);

        if (currentSessions == null) {
            currentSessions = new HashSet<>();
        }

        currentSessions.add(session);
        sessions.put(gameID, currentSessions);

        if (currentGames == null) {
            currentGames = new HashSet<>();
        }

        currentGames.add(gameID);
        reverseSessions.put(session, currentGames);

        return currentSessions;
    }

    private void sendConnectMessages(Session rootSession, HashSet<Session> currentSessions,
            UserGameCommand command, String username, ChessGame.TeamColor teamColor, GameData game)
            throws IOException {

        sendServerMessage(rootSession, new LoadGameMessage(game.game()));

        for (var ses : currentSessions) {
            if (ses.equals(rootSession)) {
                continue;
            }

            StringBuilder message = new StringBuilder();
            message.append(username).append(" has joined as ");
            if (teamColor == null) {
                message.append("an observer");
            } else {
                message.append(teamColor.toString().toLowerCase());
            }

            sendServerMessage(ses, new NotificationMessage(message.toString()));
        }
    }

    private void makeMove(Session rootSession, UserGameCommand command, String username,
            ChessGame.TeamColor teamColor, GameData gameData) throws Exception {

        assertNotObserver(teamColor, "observers cannot make moves");

        int gameID = command.getGameID();
        HashSet<Session> currentSessions = sessions.get(gameID);
        verifySession(rootSession, currentSessions);

        ChessGame userGame = gameData.game();
        assertPlayable(userGame);
        verifyTurn(teamColor, userGame);

        userGame.makeMove(command.getMove());
        gameService.saveGame(gameID, command.getAuthToken(), userGame);

        sendMakeMoveMessages(rootSession, currentSessions, username, command.getMove(), userGame,
                gameData);
    }

    private void assertNotObserver(ChessGame.TeamColor teamColor, String message) throws Exception {
        if (teamColor == null) {
            throw new Exception(message);
        }
    }

    private void verifySession(Session rootSession, HashSet<Session> currentSessions) {
        if (currentSessions == null || !currentSessions.contains(rootSession)) {
            throw new NullPointerException("the game ID does not match the current session");
        }
    }

    private void verifyTurn(ChessGame.TeamColor teamColor, ChessGame userGame) throws Exception {
        if (teamColor != userGame.getTeamTurn()) {
            throw new Exception("it is not your turn");
        }
    }

    private void sendMakeMoveMessages(Session rootSession, HashSet<Session> currentSessions,
            String username, ChessMove move, ChessGame game, GameData gameData) throws IOException {
        for (var ses : currentSessions) {
            sendServerMessage(ses, new LoadGameMessage(game));

            if (ses != rootSession) {
                String message = parseMove(move, username);
                sendServerMessage(ses, new NotificationMessage(message));
            }

            sendGameStateMessage(ses, game, gameData);
        }
    }

    private void sendGameStateMessage(Session ses, ChessGame game, GameData data)
            throws IOException {

        ChessGame.TeamColor[] teams = {ChessGame.TeamColor.WHITE, ChessGame.TeamColor.BLACK};
        String[] usernames = {data.whiteUsername(), data.blackUsername()};
        String[] statuses = {" is in check", " is in checkmate", " is in stalemate"};

        String message = null;
        for (int i = 0; i < teams.length && message == null; i++) {
            if (game.isInCheck(teams[i])) {
                message = usernames[i] + statuses[0];
            } else if (game.isInCheckmate(teams[i])) {
                message = usernames[i] + statuses[1];
            } else if (game.isInStalemate(teams[i])) {
                message = usernames[i] + statuses[2];
            }
        }

        if (message != null) {
            sendServerMessage(ses, new NotificationMessage(message));
        }
    }

    private String parseMove(ChessMove move, String username) {
        var builder = new StringBuilder();
        builder.append(username).append(" has moved from ");
        builder.append(columns.get(move.getStartPosition().getColumn()));
        builder.append(move.getStartPosition().getRow());
        builder.append(" to ");
        builder.append(columns.get(move.getEndPosition().getColumn()));
        builder.append(move.getEndPosition().getRow());
        if (move.getPromotionPiece() != null) {
            builder.append(" and promoted to a ");
            builder.append(move.getPromotionPiece().toString().toLowerCase());
        }
        builder.append(".");

        return builder.toString();
    }

    private void leaveGame(Session rootSession, UserGameCommand command, String username,
            ChessGame.TeamColor teamColor) throws Exception {
        int gameID = command.getGameID();
        var currentSessions = sessions.get(gameID);
        verifySession(rootSession, currentSessions);

        if (teamColor != null) {
            gameService.leaveGame(gameID, command.getAuthToken(), teamColor);
        }

        String message = username + " has left the game";
        cleanWebSocketSessions(rootSession, 0, message);
        sendLeaveMessages(rootSession, currentSessions, message);
    }

    private void sendLeaveMessages(Session rootSession, HashSet<Session> currentSessions,
            String message) throws IOException {
        for (var ses : currentSessions) {
            if (ses.equals(rootSession)) {
                continue;
            }

            sendServerMessage(ses, new NotificationMessage(message));
        }
    }

    private void resign(Session rootSession, UserGameCommand command, String username,
            ChessGame.TeamColor teamColor, GameData data) throws Exception {

        int gameID = command.getGameID();
        var currentSessions = sessions.get(gameID);
        verifySession(rootSession, currentSessions);
        assertNotObserver(teamColor, "observers cannot resign");

        var game = data.game();
        assertPlayable(game);

        game.setPlayable(false);
        gameService.saveGame(command.getGameID(), command.getAuthToken(), game);

        String message = username + " has resigned";
        sendResignMessages(rootSession, currentSessions, message);
    }

    private void assertPlayable(ChessGame game) throws Exception {
        if (!game.getPlayable()) {
            throw new Exception("the game can no longer be played");
        }
    }

    private void sendResignMessages(Session rootSession, HashSet<Session> currentSessions,
            String message) throws IOException {
        for (var ses : currentSessions) {
            sendServerMessage(ses, new NotificationMessage(message));
        }
    }

    private void createRoutes() {
        Spark.webSocket("/ws", Server.class);
        Spark.delete("/db", this::clear);
        Spark.post("/user", this::register);
        Spark.post("/session", this::login);
        Spark.delete("/session", this::logout);
        Spark.post("/game", this::createGame);
        Spark.put("/game", this::joinGame);
        Spark.get("/game", this::listGames);
    }

    private Object clear(Request req, Response res) throws DataAccessException {
        res.type("application/json");

        try {
            clearService.clearAuths();
            clearService.clearGames();
            clearService.clearUsers();
        } catch (RuntimeException e) {
            res.status(500);
            return serializer.toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return "{}";
    }

    private Object register(Request req, Response res) {
        res.type("application/json");
        LoginResponse response;

        try {
            response = userService.register(serializer.fromJson(req.body(), RegisterRequest.class));
        } catch (JsonSyntaxException e) {
            res.status(400);
            return serializer.toJson(new ErrorResponse("Error: bad request"));
        } catch (AuthorizationException e) {
            res.status(401);
            return serializer.toJson(new ErrorResponse("Error: unauthorized"));
        } catch (DataAccessException e) {
            res.status(403);
            return serializer.toJson(new ErrorResponse("Error: already taken"));
        } catch (Throwable e) {
            res.status(500);
            return serializer.toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return serializer.toJson(response);
    }

    private Object login(Request req, Response res) {
        res.type("application/json");
        LoginResponse response;

        try {
            response = userService.login(serializer.fromJson(req.body(), LoginRequest.class));
        } catch (JsonSyntaxException e) {
            res.status(400);
            return serializer.toJson(new ErrorResponse("Error: bad request"));
        } catch (AuthorizationException e) {
            res.status(401);
            return serializer.toJson(new ErrorResponse("Error: unauthorized"));
        } catch (Throwable e) {
            res.status(500);
            return serializer.toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return serializer.toJson(response);
    }

    private Object logout(Request req, Response res) {
        res.type("application/json");

        try {
            userService.logout(new LogoutRequest(req.headers("authorization")));
        } catch (AuthorizationException e) {
            res.status(401);
            return serializer.toJson(new ErrorResponse("Error: unauthorized"));
        } catch (Throwable e) {
            res.status(500);
            return serializer.toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return "{}";
    }

    private Object createGame(Request req, Response res) {
        res.type("application/json");
        CreateGameResponse response;

        try {
            response = gameService.createGame(req.headers("authorization"),
                    serializer.fromJson(req.body(), CreateGameRequest.class));
        } catch (JsonSyntaxException e) {
            res.status(400);
            return serializer.toJson(new ErrorResponse("Error: bad request"));
        } catch (AuthorizationException e) {
            res.status(401);
            return serializer.toJson(new ErrorResponse("Error: unauthorized"));
        } catch (Throwable e) {
            res.status(500);
            return serializer.toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return serializer.toJson(response);
    }

    private Object joinGame(Request req, Response res) {
        res.type("application/json");

        try {
            gameService.joinGame(req.headers("authorization"),
                    serializer.fromJson(req.body(), JoinGameRequest.class));
        } catch (NullPointerException | JsonSyntaxException e) {
            res.status(400);
            return serializer.toJson(new ErrorResponse("Error: bad request"));
        } catch (AuthorizationException e) {
            res.status(401);
            return serializer.toJson(new ErrorResponse("Error: unauthorized"));
        } catch (DataAccessException e) {
            res.status(403);
            return serializer.toJson(new ErrorResponse("Error: already taken"));
        } catch (Throwable e) {
            res.status(500);
            return serializer.toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return "{}";
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        res.type("application/json");
        ListGameResponse response;

        try {
            response = gameService.listGames(req.headers("authorization"));
        } catch (AuthorizationException e) {
            res.status(401);
            return serializer.toJson(new ErrorResponse("Error: unauthorized"));
        } catch (Throwable e) {
            res.status(500);
            return serializer.toJson(new ErrorResponse(e.getMessage()));
        }

        return serializer.toJson(response);
    }
}
