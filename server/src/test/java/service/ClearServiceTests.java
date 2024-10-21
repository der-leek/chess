package service;

import dataaccess.*;
import model.*;
import chess.ChessGame;
import java.util.Random;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ClearServiceTests {

    private DataAccess dataAccess;
    private ClearService service;

    @Test
    public void clearUsersPositive() {
        dataAccess = new MemoryDataAccess();
        service = new ClearService(dataAccess);

        dataAccess.createUser("username", new UserData("username", "password", "email"));
        service.clearUsers();
        Assertions.assertTrue(dataAccess.isUserDataEmpty());
    }

    @Test
    public void clearUsersNegative() {
        dataAccess = null;
        service = new ClearService(dataAccess);

        Assertions.assertThrows(NullPointerException.class, () -> service.clearUsers());
    }

    @Test
    public void clearAuthsPositive() {
        dataAccess = new MemoryDataAccess();
        service = new ClearService(dataAccess);

        dataAccess.createAuth("username", new AuthData("authToken", "username"));
        service.clearAuths();
        Assertions.assertTrue(dataAccess.isAuthDataEmpty());
    }

    @Test
    public void clearAuthsNegative() {
        dataAccess = null;
        service = new ClearService(dataAccess);

        Assertions.assertThrows(NullPointerException.class, () -> service.clearAuths());
    }

    @Test
    public void clearGamesPositive() {
        dataAccess = new MemoryDataAccess();
        service = new ClearService(dataAccess);

        Integer gameID = new Random().nextInt(10000);
        GameData gd = new GameData(gameID, "", "", "new game", new ChessGame());
        dataAccess.createGame(gameID, gd);

        service.clearGames();
        Assertions.assertTrue(dataAccess.isGameDataEmpty());
    }

    @Test
    public void clearGamesNegative() {
        dataAccess = null;
        service = new ClearService(dataAccess);

        Assertions.assertThrows(NullPointerException.class, () -> service.clearGames());
    }
}
