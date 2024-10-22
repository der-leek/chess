package service;

import model.*;
import dataaccess.*;
import chess.ChessGame;
import requests_responses.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServiceTests {

    private DataAccess dataAccess;
    private GameService gameService;

    @BeforeEach
    public void setup() {
        dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
    }

    @Test
    public void createGameInvalidAuth() {
        dataAccess.createAuth("auth", new AuthData("auth", "user"));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        var request = new CreateGameRequest("game");
        Assertions.assertThrows(AuthorizationException.class,
                () -> gameService.createGame("wrongAuth", request));
    }

    @Test
    public void createGameSuccess() throws AuthorizationException {
        String authToken = "auth";
        dataAccess.createAuth(authToken, new AuthData(authToken, "user"));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        String gameName = "game";
        var request = new CreateGameRequest(gameName);
        var response = gameService.createGame(authToken, request);
        var gameData = dataAccess.findGameData(response.gameID());

        Assertions.assertInstanceOf(Integer.class, response.gameID());
        Assertions.assertInstanceOf(ChessGame.class, gameData.game());
        Assertions.assertEquals(gameName, gameData.gameName());
        Assertions.assertEquals("", gameData.whiteUsername());
        Assertions.assertEquals("", gameData.blackUsername());
    }

    @Test
    public void joinGameInvalidAuth() {
        dataAccess.createAuth("auth", new AuthData("auth", "user"));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        var request = new JoinGameRequest("white", 315);
        Assertions.assertThrows(AuthorizationException.class,
                () -> gameService.joinGame("wrongAuth", request));
    }

    @Test
    public void joinGameInvalidGameID() {
        String authToken = "auth";
        dataAccess.createAuth(authToken, new AuthData(authToken, "user"));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        int realGameID = 30;
        dataAccess.createGame(realGameID,
                new GameData(realGameID, "", "", "game", new ChessGame()));
        Assertions.assertTrue(!dataAccess.isGameDataEmpty());

        var request = new JoinGameRequest("WHITE", 3014);
        Assertions.assertThrows(NullPointerException.class,
                () -> gameService.joinGame(authToken, request));
    }

    @Test
    public void joinGameUsernameTaken() {
        String authToken = "auth";
        dataAccess.createAuth(authToken, new AuthData(authToken, "user"));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        int gameID = 30;
        dataAccess.createGame(gameID, new GameData(gameID, "white", "", "game", new ChessGame()));
        Assertions.assertTrue(!dataAccess.isGameDataEmpty());

        var request = new JoinGameRequest("WHITE", gameID);
        Assertions.assertThrows(DataAccessException.class,
                () -> gameService.joinGame(authToken, request));
    }

    @Test
    public void joinGameSuccess() throws AuthorizationException, DataAccessException {
        String authToken = "auth";
        String blackUsername = "black";
        dataAccess.createAuth(authToken, new AuthData(authToken, blackUsername));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        int gameID = 30;
        dataAccess.createGame(gameID, new GameData(gameID, "white", "", "game", new ChessGame()));
        Assertions.assertTrue(!dataAccess.isGameDataEmpty());

        var request = new JoinGameRequest("BLACK", gameID);
        gameService.joinGame(authToken, request);

        var joinedGame = dataAccess.findGameData(gameID);
        Assertions.assertEquals(blackUsername, joinedGame.blackUsername());
    }
}
