package server;

import spark.*;
import service.*;
import dataaccess.*;
import requests_responses.*;
import com.google.gson.JsonSyntaxException;

public class Server {
    private final DataAccess dataAccess;
    private final ClearService clearService;
    private final UserService userService;

    public Server() {
        dataAccess = new MemoryDataAccess();
        clearService = new ClearService(dataAccess);
        userService = new UserService(dataAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        createRoutes();

        // This line initializes the server and can be removed once you have a functioning endpoint
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    private void createRoutes() {
        Spark.delete("/db", this::delete);
        Spark.post("/user", this::register);
    }

    private Object register(Request req, Response res) {
        res.type("application/json");

        RegisterResponse response;

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
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse(e.toString()));
        }

        res.status(200);
        return new Serializer<RegisterResponse>().toJson(response);
    }

    private Object delete(Request req, Response res) {
        res.type("application/json");

        if (!req.body().isEmpty()) {
            res.status(400);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse("Error: bad request"));
        }

        try {
            clearService.clearUsers();
            clearService.clearGames();
            clearService.clearAuths();
        } catch (RuntimeException e) {
            res.status(500);
            return new Serializer<ErrorResponse>().toJson(new ErrorResponse(e.toString()));
        }

        res.status(200);
        return "{}";
    }
}
