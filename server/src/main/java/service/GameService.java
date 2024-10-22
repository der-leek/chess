package service;

import model.*;
import dataaccess.*;
import chess.ChessGame;
import requests_responses.*;
import java.util.Random;

public class GameService {

    private DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public CreateGameResponse createGame(String authToken, CreateGameRequest req)
            throws DataAccessException {
        authorize(authToken);

        int gameID_Limit = 9999;
        int gameID = createRandomGameID(gameID_Limit);
        var gameData = new GameData(gameID, "", "", req.gameName(), new ChessGame());
        dataAccess.createGame(gameID, gameData);

        return new CreateGameResponse(gameID);
    }

    private void authorize(String authToken) throws DataAccessException {
        var authData = dataAccess.findAuthData(authToken);
        if (authData == null) {
            throw new DataAccessException("Unauthorized");
        }
    }

    private Integer createRandomGameID(int upperLimit) {
        var random = new Random();
        int gameID = random.nextInt(upperLimit) + 1;

        while (dataAccess.findGameData(gameID) != null) {
            gameID = random.nextInt(upperLimit) + 1;
        }

        return gameID;
    }
}
