package dataaccess;

import model.*;
import service.AuthorizationException;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import chess.ChessGame;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class DAOTests {

    private DataAccess dataAccess;
    private UserData testUserData;
    private AuthData testAuthData;
    private GameData testGameData;

    public DAOTests() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
        testUserData = new UserData("user", "pass", "me@mail.com");
        testAuthData = new AuthData("3f9a9c9b-6745-4a7f-8cfc-bc11c8ab2b35", "user");
        testGameData = new GameData(34, null, null, "game", new ChessGame());
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        dataAccess.clearGameDAO();
        dataAccess.clearAuthDAO();
        dataAccess.clearUserDAO();
    }

    @Test
    public void createNewUserSuccess() throws DataAccessException {
        dataAccess.createUser(testUserData);
        var data = dataAccess.findUserData(testUserData.username());

        Assertions.assertEquals(data.username(), testUserData.username());
        Assertions.assertEquals(data.email(), testUserData.email());
        Assertions.assertTrue(BCrypt.checkpw(testUserData.password(), data.password()));
    }

    @Test
    public void createNewUserOverwrite() throws DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createUser(
                new UserData(testUserData.username(), testUserData.password(), "new@mail.com"));
        var data = dataAccess.findUserData(testUserData.username());

        Assertions.assertEquals(testUserData.username(), data.username());
        Assertions.assertTrue(BCrypt.checkpw(testUserData.password(), data.password()));
        Assertions.assertEquals("new@mail.com", data.email());
        Assertions.assertNotEquals(testUserData.password(), data.password());
    }

    @Test
    public void findUserSuccess() throws DataAccessException {
        dataAccess.createUser(testUserData);
        var data = dataAccess.findUserData(testUserData.username());

        Assertions.assertNotNull(data);
    }

    @Test
    public void findNonUser() throws DataAccessException {
        dataAccess.createUser(testUserData);
        var data = dataAccess.findUserData("badUser");

        Assertions.assertNull(data);
    }

    @Test
    public void findUserBadUsername() throws DataAccessException {
        dataAccess.createUser(testUserData);

        Assertions.assertThrows(DataAccessException.class,
                () -> dataAccess.findUserData("bad_user;"));
    }

    @Test
    public void createNewAuthSuccess() throws AuthorizationException, DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createAuth(testAuthData);
        var data = dataAccess.findAuthData(testAuthData.authToken());

        Assertions.assertEquals(data.authToken(), testAuthData.authToken());
        Assertions.assertEquals(data.username(), testAuthData.username());
    }

    @Test
    public void createNewAuthInvalidUsername() throws DataAccessException {
        Assertions.assertThrows(DataAccessException.class,
                () -> dataAccess.createAuth(new AuthData(testAuthData.authToken(), "badUser")));

    }

    @Test
    public void createNewAuthBadAuthToken() throws DataAccessException {
        dataAccess.createUser(testUserData);

        Assertions.assertThrows(DataAccessException.class,
                () -> dataAccess.createAuth(new AuthData("badAuth", testAuthData.username())));

    }

    @Test
    public void findAuthBadAuth() throws AuthorizationException, DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createAuth(testAuthData);
        var data = dataAccess.findAuthData("3d9a9c9b-6745-4a7f-8cfc-bc11c8ab2b35");

        Assertions.assertNull(data);
    }

    @Test
    public void findNonAuth() throws DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createAuth(testAuthData);

        Assertions.assertThrows(AuthorizationException.class,
                () -> dataAccess.findAuthData("badAuth"));
    }

    @Test
    public void deleteAuthSuccess() throws AuthorizationException, DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createAuth(testAuthData);
        dataAccess.deleteAuth(testAuthData.authToken());
        var data = dataAccess.findAuthData(testAuthData.authToken());

        Assertions.assertNull(data);
    }

    @Test
    public void deleteAuthNonToken() throws AuthorizationException, DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createAuth(testAuthData);
        dataAccess.deleteAuth("3d9a9c9b-6745-4a7f-8cfc-bc11c8ab2b35");
        var data = dataAccess.findAuthData(testAuthData.authToken());

        Assertions.assertNotNull(data);
    }

    @Test
    public void deleteAuthBadToken() throws DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createAuth(testAuthData);

        Assertions.assertThrows(DataAccessException.class, () -> dataAccess.deleteAuth("badToken"));
    }

    @Test
    public void createGameSuccess() throws DataAccessException {
        dataAccess.createGame(testGameData);
        var data = dataAccess.findGameData(testGameData.gameID());

        Assertions.assertEquals(testGameData.gameID(), data.gameID());
        Assertions.assertEquals(testGameData.whiteUsername(), data.whiteUsername());
        Assertions.assertEquals(testGameData.blackUsername(), data.blackUsername());
        Assertions.assertEquals(testGameData.gameName(), data.gameName());
        Assertions.assertEquals(testGameData.game(), data.game());
    }

    @Test
    public void createGameOverwrite() throws DataAccessException {
        dataAccess.createGame(testGameData);
        Assertions.assertThrows(DataAccessException.class,
                () -> dataAccess.createGame(new GameData(testGameData.gameID(), null, null,
                        "differentGameName", testGameData.game())));

    }

    @Test
    public void findGameSuccess() throws DataAccessException {
        dataAccess.createGame(testGameData);
        var data = dataAccess.findGameData(testGameData.gameID());

        Assertions.assertNotNull(data);
        Assertions.assertEquals(testGameData.gameID(), data.gameID());
        Assertions.assertEquals(testGameData.whiteUsername(), data.whiteUsername());
        Assertions.assertEquals(testGameData.blackUsername(), data.blackUsername());
        Assertions.assertEquals(testGameData.gameName(), data.gameName());
        Assertions.assertEquals(testGameData.game(), data.game());
    }

    @Test
    public void findGameWrongID() throws DataAccessException {
        dataAccess.createGame(testGameData);
        var data = dataAccess.findGameData(testGameData.gameID() + 3);

        Assertions.assertNull(data);
    }

    @Test
    public void updateGameSuccess() throws AuthorizationException, DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createGame(testGameData);
        dataAccess.updateGame(new GameData(testGameData.gameID(), testUserData.username(), null,
                testGameData.gameName(), testGameData.game()));
        var data = dataAccess.findGameData(testGameData.gameID());

        Assertions.assertEquals(testGameData.gameID(), data.gameID());
        Assertions.assertEquals(testUserData.username(), data.whiteUsername());
        Assertions.assertEquals(testGameData.blackUsername(), data.blackUsername());
        Assertions.assertEquals(testGameData.gameName(), data.gameName());
        Assertions.assertEquals(testGameData.game(), data.game());
    }

    @Test
    public void updateGameBadUsername() throws DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createGame(testGameData);

        Assertions.assertThrows(DataAccessException.class,
                () -> dataAccess.updateGame(new GameData(testGameData.gameID(), "badUs3r", null,
                        testGameData.gameName(), testGameData.game())));

    }

    @Test
    public void listGamesSuccess() throws DataAccessException {
        dataAccess.createGame(testGameData);
        var newGameData =
                new GameData(testGameData.gameID() + 1, null, null, "newgame", new ChessGame());
        dataAccess.createGame(newGameData);
        var games = dataAccess.listGames();

        Assertions.assertEquals(games.get(0), testGameData);
        Assertions.assertEquals(games.get(1), newGameData);
    }

    @Test
    public void listGamesNoGames() throws DataAccessException {
        var games = dataAccess.listGames();

        Assertions.assertTrue(games.isEmpty());
    }

    @Test
    public void clearUserSuccess() throws DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.clearUserDAO();

        Assertions.assertTrue(dataAccess.isUserDataEmpty());
    }

    @Test
    public void clearAuthSuccess() throws DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createAuth(testAuthData);
        dataAccess.clearAuthDAO();

        Assertions.assertTrue(dataAccess.isAuthDataEmpty());
    }

    @Test
    public void clearGameSuccess() throws DataAccessException {
        dataAccess.createGame(testGameData);
        dataAccess.clearGameDAO();

        Assertions.assertTrue(dataAccess.isGameDataEmpty());
    }
}
