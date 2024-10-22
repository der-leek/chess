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
        Assertions.assertThrows(DataAccessException.class,
                () -> gameService.createGame("wrongAuth", request));
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
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
}
