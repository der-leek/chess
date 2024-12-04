package server;

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
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;

@WebSocket
public class Server {
    private Map<Integer, HashSet<Session>> sessions;
    private final DataAccess dataAccess;
    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;
    private final Serializer serializer = new Serializer();

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

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            var command = serializer.fromJson(message, UserGameCommand.class);

            switch (command.getCommandType()) {
                case CONNECT -> connect(session, (ConnectCommand) command);
                case MAKE_MOVE -> makeMove(session, (MakeMoveCommand) command);
                case LEAVE -> leaveGame(session, (LeaveGameCommand) command);
                case RESIGN -> resign(session, (ResignCommand) command);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            sendServerMessage(session, new ErrorMessage("Error: " + ex.getMessage()));
        }
    }

    private void sendServerMessage(Session session, ServerMessage message) throws IOException {
        session.getRemote().sendString(serializer.toJson(message));
    }

    private void connect(Session session, ConnectCommand command)
            throws AuthorizationException, DataAccessException, IOException {
        var currentSessions = addSessionToGame(session, command.getGameID());
        sendConnectMessages(session, command, currentSessions,
                findGame(command.getAuthToken(), command.getGameID()));
    }

    private HashSet<Session> addSessionToGame(Session session, int gameID) {
        var currentSessions = sessions.get(gameID);

        if (currentSessions == null) {
            currentSessions = new HashSet<>();
        }

        currentSessions.add(session);
        sessions.put(gameID, currentSessions);
        return currentSessions;
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

    private void sendConnectMessages(Session rootSession, ConnectCommand command,
            HashSet<Session> currentSessions, GameData game) throws IOException {

        var teamColor = command.getTeamColor();
        sendServerMessage(rootSession, new LoadGameMessage(game.game(), teamColor));

        for (var ses : currentSessions) {
            if (ses == rootSession) {
                continue;
            }

            StringBuilder message = new StringBuilder();
            message.append(command.getUsername()).append(" has joined as ");
            if (teamColor == null) {
                message.append("an observer");
            } else {
                message.append(teamColor.toString().toLowerCase());
            }

            sendServerMessage(ses, new NotificationMessage(message.toString()));
        }
    }

    private void makeMove(Session rootSession, MakeMoveCommand command) {

    }

    private void leaveGame(Session rootSession, LeaveGameCommand command) {

    }

    private void resign(Session rootSession, ResignCommand command) {

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
