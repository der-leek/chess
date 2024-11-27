package service;

import model.*;
import dataaccess.*;
import chess.ChessGame;
import requests.*;
import java.util.ArrayList;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class GameServiceTests {

    private DataAccess dataAccess;
    private GameService gameService;

    @BeforeEach
    public void setup() {
        // WARNING: These tests are incompatible with MySqlDataAccess
        dataAccess = new MemoryDataAccess();
        gameService = new GameService(dataAccess);
    }

    @Test
    public void createGameInvalidAuth() throws AuthorizationException, DataAccessException {
        dataAccess.createAuth(new AuthData("auth", "user"));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        var request = new CreateGameRequest("game");
        Assertions.assertThrows(AuthorizationException.class,
                () -> gameService.createGame("wrongAuth", request));
    }

    @Test
    public void createGameSuccess() throws AuthorizationException, DataAccessException {
        String authToken = "auth";
        dataAccess.createAuth(new AuthData(authToken, "user"));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        String gameName = "game";
        var request = new CreateGameRequest(gameName);
        var response = gameService.createGame(authToken, request);
        var gameData = dataAccess.findGameData(response.gameID());

        Assertions.assertInstanceOf(Integer.class, response.gameID());
        Assertions.assertInstanceOf(ChessGame.class, gameData.game());
        Assertions.assertEquals(gameName, gameData.gameName());
        Assertions.assertEquals(null, gameData.whiteUsername());
        Assertions.assertEquals(null, gameData.blackUsername());
    }

    @Test
    public void joinGameInvalidAuth() throws AuthorizationException, DataAccessException {
        dataAccess.createAuth(new AuthData("auth", "user"));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        var request = new JoinGameRequest(ChessGame.TeamColor.WHITE, 315);
        Assertions.assertThrows(AuthorizationException.class,
                () -> gameService.joinGame("wrongAuth", request));
    }

    @Test
    public void joinGameInvalidGameID() throws AuthorizationException, DataAccessException {
        String authToken = "auth";
        dataAccess.createAuth(new AuthData(authToken, "user"));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        int realGameID = 30;
        dataAccess.createGame(new GameData(realGameID, null, null, "game", new ChessGame()));
        Assertions.assertTrue(!dataAccess.isGameDataEmpty());

        var request = new JoinGameRequest(ChessGame.TeamColor.WHITE, 3014);
        Assertions.assertThrows(NullPointerException.class,
                () -> gameService.joinGame(authToken, request));
    }

    @Test
    public void joinGameUsernameTaken() throws AuthorizationException, DataAccessException {
        String authToken = "auth";
        dataAccess.createAuth(new AuthData(authToken, "user"));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        int gameID = 30;
        dataAccess.createGame(new GameData(gameID, "white", null, "game", new ChessGame()));
        Assertions.assertTrue(!dataAccess.isGameDataEmpty());

        var request = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID);
        Assertions.assertThrows(DataAccessException.class,
                () -> gameService.joinGame(authToken, request));
    }

    @Test
    public void joinGameAddWhite() throws AuthorizationException, DataAccessException {
        String authToken = "auth";
        String whiteUsername = "white";
        dataAccess.createAuth(new AuthData(authToken, whiteUsername));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        int gameID = 30;
        dataAccess.createGame(new GameData(gameID, null, null, "game", new ChessGame()));
        Assertions.assertTrue(!dataAccess.isGameDataEmpty());

        var request = new JoinGameRequest(ChessGame.TeamColor.WHITE, gameID);
        gameService.joinGame(authToken, request);

        var joinedGame = dataAccess.findGameData(gameID);
        Assertions.assertEquals(whiteUsername, joinedGame.whiteUsername());
    }

    @Test
    public void joinGameAddBlack() throws AuthorizationException, DataAccessException {
        String authToken = "auth";
        String blackUsername = "black";
        dataAccess.createAuth(new AuthData(authToken, blackUsername));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        int gameID = 30;
        dataAccess.createGame(new GameData(gameID, null, null, "game", new ChessGame()));
        Assertions.assertTrue(!dataAccess.isGameDataEmpty());

        var request = new JoinGameRequest(ChessGame.TeamColor.BLACK, gameID);
        gameService.joinGame(authToken, request);

        var joinedGame = dataAccess.findGameData(gameID);
        Assertions.assertEquals(blackUsername, joinedGame.blackUsername());
    }

    @Test
    public void listGamesInvalidAuth() throws AuthorizationException, DataAccessException {
        dataAccess.createAuth(new AuthData("auth", "user"));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        Assertions.assertThrows(AuthorizationException.class,
                () -> gameService.listGames("wrongAuth"));
    }

    @Test
    public void listGamesSuccess() throws AuthorizationException, DataAccessException {
        String authToken = "auth";
        dataAccess.createAuth(new AuthData(authToken, "user"));
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        int gameID = 3;
        String gameName = "game";
        ChessGame game = new ChessGame();
        dataAccess.createGame(new GameData(gameID, null, null, gameName, game));
        Assertions.assertTrue(!dataAccess.isGameDataEmpty());

        ArrayList<GameData> expectedGames = new ArrayList<GameData>();
        expectedGames.add(new GameData(gameID, null, null, gameName, game));
        Assertions.assertEquals(expectedGames, dataAccess.listGames());
    }
}
