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
            throws AuthorizationException, DataAccessException {
        authorize(authToken);

        int gameIDLimit = 9999;
        int gameID = createRandomGameID(gameIDLimit);
        var gameData = new GameData(gameID, null, null, req.gameName(), new ChessGame());
        dataAccess.createGame(gameData);

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
        dataAccess.updateGame(joinedGame);
    }

    public void saveGame(int gameID, String authToken, ChessGame newGameState)
            throws AuthorizationException, DataAccessException {
        authorize(authToken);

        var oldGame = dataAccess.findGameData(gameID);
        if (oldGame == null) {
            throw new NullPointerException("Game does not exist");
        }

        var updatedGame = new GameData(gameID, oldGame.whiteUsername(), oldGame.blackUsername(),
                oldGame.gameName(), newGameState);
        dataAccess.updateGame(updatedGame);
    }

    public void leaveGame(int gameID, String authToken, ChessGame.TeamColor teamColor)
            throws AuthorizationException, DataAccessException {

        authorize(authToken);

        var oldGame = dataAccess.findGameData(gameID);
        if (oldGame == null) {
            throw new NullPointerException("Game does not exist");
        }

        var leftGame = oldGame.updateUsername(teamColor, null);
        dataAccess.updateGame(leftGame);
    }

    public GameData findGame(int gameID, String authToken)
            throws AuthorizationException, DataAccessException {
        authorize(authToken);
        return dataAccess.findGameData(gameID);
    }

    public ListGameResponse listGames(String authToken)
            throws AuthorizationException, DataAccessException {
        authorize(authToken);
        return new ListGameResponse(dataAccess.listGames());
    }

    private AuthData authorize(String authToken)
            throws AuthorizationException, DataAccessException {
        var authData = dataAccess.findAuthData(authToken);
        if (authData == null) {
            throw new AuthorizationException("Unauthorized");
        }
        return authData;
    }

    private Integer createRandomGameID(int upperLimit) throws DataAccessException {
        var random = new Random();
        int gameID = random.nextInt(upperLimit) + 1;

        while (dataAccess.findGameData(gameID) != null) {
            gameID = random.nextInt(upperLimit) + 1;
        }

        return gameID;
    }
}
