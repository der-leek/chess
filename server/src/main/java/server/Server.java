package server;

import spark.*;
import service.*;
import dataaccess.*;
import requests_results.ClearResponse;

public class Server {
    private final DataAccess dataAccess;
    private final ClearService clearService;

    public Server() {
        dataAccess = new MemoryDataAccess();
        clearService = new ClearService(dataAccess);
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
        return res;
    }

    private Object delete(Request req, Response res) {
        res.type("application/json");

        if (!req.body().isEmpty()) {
            res.status(400);
            return new Serializer<ClearResponse>().toJson(new ClearResponse("/db has no request body"));
        }

        try {
            clearService.clearUsers();
            clearService.clearGames();
            clearService.clearAuths();
        } catch (RuntimeException e) {
            res.status(500);
            return new Serializer<ClearResponse>().toJson(new ClearResponse("There was an error clearing the database"));
        }

        res.status(200);
        return "{}";
    }
}
