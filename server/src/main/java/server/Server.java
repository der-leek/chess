package server;

import spark.*;
import service.*;
import dataaccess.*;
import requests.*;
import responses.*;
import serializer.*;
import com.google.gson.JsonSyntaxException;

public class Server {
    private final DataAccess dataAccess;
    private final ClearService clearService;
    private final UserService userService;
    private final GameService gameService;

    public Server() {
        try {
            dataAccess = new MySqlDataAccess();
        } catch (DataAccessException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        clearService = new ClearService(dataAccess);
        userService = new UserService(dataAccess);
        gameService = new GameService(dataAccess);
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

    private void createRoutes() {
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

        if (!req.body().isEmpty()) {
            res.status(400);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: bad request"));
        }

        try {
            clearService.clearAuths();
            clearService.clearGames();
            clearService.clearUsers();
        } catch (RuntimeException e) {
            res.status(500);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return "{}";
    }

    private Object register(Request req, Response res) {
        res.type("application/json");
        LoginResponse response;

        try {
            response = userService.register(
                    new Serializer<RegisterRequest>().fromJson(req.body(), RegisterRequest.class));
        } catch (JsonSyntaxException e) {
            res.status(400);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: bad request"));
        } catch (DataAccessException e) {
            res.status(403);
            return new Serializer<ErrorResponse>()
                    .toJson(new ErrorResponse("Error: already taken"));
        } catch (Throwable e) {
            res.status(500);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return new Serializer<LoginResponse>().toJson(response);
    }

    private Object login(Request req, Response res) {
        res.type("application/json");
        LoginResponse response;

        try {
            response = userService
                    .login(new Serializer<LoginRequest>().fromJson(req.body(), LoginRequest.class));
        } catch (JsonSyntaxException e) {
            res.status(400);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: bad request"));
        } catch (DataAccessException e) {
            res.status(401);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: unauthorized"));
        } catch (Throwable e) {
            res.status(500);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return new Serializer<LoginResponse>().toJson(response);
    }

    private Object logout(Request req, Response res) {
        res.type("application/json");

        if (!req.body().isEmpty()) {
            res.status(400);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: bad request"));
        }

        try {
            userService.logout(new LogoutRequest(req.headers("authorization")));
        } catch (AuthorizationException e) {
            res.status(401);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: unauthorized"));
        } catch (Throwable e) {
            res.status(500);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return "{}";
    }

    private Object createGame(Request req, Response res) {
        res.type("application/json");
        CreateGameResponse response;

        try {
            response = gameService.createGame(req.headers("authorization"),
                    new Serializer<CreateGameRequest>().fromJson(req.body(),
                            CreateGameRequest.class));
        } catch (JsonSyntaxException e) {
            res.status(400);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: bad request"));
        } catch (AuthorizationException e) {
            res.status(401);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: unauthorized"));
        } catch (Throwable e) {
            res.status(500);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return new Serializer<CreateGameResponse>().toJson(response);
    }

    private Object joinGame(Request req, Response res) {
        res.type("application/json");

        if (req.body().isEmpty()) {
            res.status(400);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: bad request"));
        }

        try {
            gameService.joinGame(req.headers("authorization"),
                    new Serializer<JoinGameRequest>().fromJson(req.body(), JoinGameRequest.class));
        } catch (AuthorizationException e) {
            res.status(401);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: unauthorized"));
        } catch (DataAccessException e) {
            res.status(403);
            return new Serializer<ErrorResponse>()
                    .toJson(new ErrorResponse("Error: already taken"));
        } catch (NullPointerException e) {
            res.status(400);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: bad request"));
        } catch (Throwable e) {
            res.status(500);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse(e.getMessage()));
        }

        res.status(200);
        return "{}";
    }

    private Object listGames(Request req, Response res) throws DataAccessException {
        res.type("application/json");
        ListGameResponse response;

        if (!req.body().isEmpty()) {
            res.status(400);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: bad request"));
        }

        try {
            response = gameService.listGames(req.headers("authorization"));
        } catch (AuthorizationException e) {
            res.status(401);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: unauthorized"));
        } catch (Throwable e) {
            res.status(500);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse(e.getMessage()));
        }

        return new Serializer<ListGameResponse>().toJson(response);
    }
}
