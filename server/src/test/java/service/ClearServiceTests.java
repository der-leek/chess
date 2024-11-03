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
    public void clearUsersSuccess() throws DataAccessException {
        dataAccess = new MemoryDataAccess();
        service = new ClearService(dataAccess);

        dataAccess.createUser(new UserData("username", "password", "email"));
        service.clearUsers();
        Assertions.assertTrue(dataAccess.isUserDataEmpty());
    }

    @Test
    public void clearUsersFail() {
        dataAccess = null;
        service = new ClearService(dataAccess);

        Assertions.assertThrows(NullPointerException.class, () -> service.clearUsers());
    }

    @Test
    public void clearAuthsSuccess() throws DataAccessException {
        dataAccess = new MemoryDataAccess();
        service = new ClearService(dataAccess);

        dataAccess.createAuth(new AuthData("authToken", "username"));
        service.clearAuths();
        Assertions.assertTrue(dataAccess.isAuthDataEmpty());
    }

    @Test
    public void clearAuthsFail() {
        dataAccess = null;
        service = new ClearService(dataAccess);

        Assertions.assertThrows(NullPointerException.class, () -> service.clearAuths());
    }

    @Test
    public void clearGamesSuccess() throws DataAccessException {
        dataAccess = new MemoryDataAccess();
        service = new ClearService(dataAccess);

        Integer gameID = new Random().nextInt(10000);
        GameData gd = new GameData(gameID, "", "", "new game", new ChessGame());
        dataAccess.createGame(gd);

        service.clearGames();
        Assertions.assertTrue(dataAccess.isGameDataEmpty());
    }

    @Test
    public void clearGamesFail() {
        dataAccess = null;
        service = new ClearService(dataAccess);

        Assertions.assertThrows(NullPointerException.class, () -> service.clearGames());
    }
}
