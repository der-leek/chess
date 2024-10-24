package service;

import model.*;
import dataaccess.*;
import chess.ChessGame;
import requests.*;
import responses.*;
import java.util.Random;

public class GameService {

    private DataAccess dataAccess;

    public GameService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public CreateGameResponse createGame(String authToken, CreateGameRequest req)
            throws AuthorizationException {
        authorize(authToken);

        int gameIDLimit = 9999;
        int gameID = createRandomGameID(gameIDLimit);
        var gameData = new GameData(gameID, null, null, req.gameName(), new ChessGame());
        dataAccess.createGame(gameID, gameData);

        return new CreateGameResponse(gameID);
    }

    public void joinGame(String authToken, JoinGameRequest req)
            throws AuthorizationException, DataAccessException {
        var authData = authorize(authToken);

        int gameID = req.gameID();
        ChessGame.TeamColor playerColor = req.playerColor();
        var oldGame = dataAccess.findGameData(gameID);

        if (oldGame == null) {
            throw new NullPointerException("Game does not exist");
        } else if (playerColor.equals(ChessGame.TeamColor.WHITE)
                && oldGame.whiteUsername() != null) {
            throw new DataAccessException("Username taken");
        } else if (playerColor.equals(ChessGame.TeamColor.BLACK)
                && oldGame.blackUsername() != null) {
            throw new DataAccessException("Username taken");
        }

        var joinedGame = oldGame.updateUsername(req.playerColor(), authData.username());
        dataAccess.updateGame(gameID, joinedGame);
    }

    public ListGameResponse listGames(String authToken) throws AuthorizationException {
        authorize(authToken);
        return new ListGameResponse(dataAccess.listGames());
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
