package service;

import model.*;
import dataaccess.*;
import chess.ChessGame;
import requests_responses.*;
import java.util.Random;
import java.util.Set;

public class GameService {

    private DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public CreateGameResponse createGame(String authToken, CreateGameRequest req)
            throws AuthorizationException {
        authorize(authToken);

        int gameID_Limit = 9999;
        int gameID = createRandomGameID(gameID_Limit);
        var gameData = new GameData(gameID, "", "", req.gameName(), new ChessGame());
        dataAccess.createGame(gameID, gameData);

        return new CreateGameResponse(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest req)
            throws AuthorizationException, DataAccessException {
        var authData = authorize(authToken);

        int gameID = req.gameID();
        String playerColor = req.playerColor();
        var oldGame = dataAccess.findGameData(gameID);

        if (oldGame == null) {
            throw new NullPointerException("Game does not exist");
        } else if (!Set.of("WHITE", "BLACK").contains(playerColor)) {
            throw new DataAccessException("Invalid player color");
        } else if (playerColor.equals("WHITE") && !oldGame.whiteUsername().equals("")) {
            throw new DataAccessException("Username taken");
        } else if (playerColor.equals("BLACK") && !oldGame.blackUsername().equals("")) {
            throw new DataAccessException("Username taken");
        }

        var joinedGame = oldGame.updateUsername(req.playerColor(), authData.username());
        dataAccess.updateGame(gameID, joinedGame);
    }

    private AuthData authorize(String authToken) throws AuthorizationException {
        var authData = dataAccess.findAuthData(authToken);
        if (authData == null) {
            throw new AuthorizationException("Unauthorized");
        }
        return authData;
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
