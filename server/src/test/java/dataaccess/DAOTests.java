package dataaccess;

import model.*;
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
    public void createNewUserDuplicateUsername() throws DataAccessException {
        dataAccess.createUser(testUserData);

        Assertions.assertThrows(DataAccessException.class,
                () -> dataAccess.createUser(new UserData(testUserData.username(),
                        testUserData.password(), testUserData.email())));
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
                () -> dataAccess.findUserData("badUs3r"));
    }

    @Test
    public void createNewAuthSuccess() throws DataAccessException {
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
    public void findAuthSuccess() throws DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createAuth(testAuthData);
        var data = dataAccess.findAuthData(testAuthData.authToken());

        Assertions.assertEquals(testAuthData.authToken(), data.authToken());
        Assertions.assertEquals(testAuthData.username(), data.username());
    }

    @Test
    public void findAuthBadAuth() throws DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createAuth(testAuthData);
        var data = dataAccess.findAuthData("3d9a9c9b-6745-4a7f-8cfc-bc11c8ab2b35");

        Assertions.assertNull(data);
    }

    @Test
    public void findNonAuth() throws DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createAuth(testAuthData);

        Assertions.assertThrows(DataAccessException.class,
                () -> dataAccess.findAuthData("badAuth"));
    }

    @Test
    public void deleteAuthSuccess() throws DataAccessException {
        dataAccess.createUser(testUserData);
        dataAccess.createAuth(testAuthData);
        dataAccess.deleteAuth(testAuthData.authToken());
        var data = dataAccess.findAuthData(testAuthData.authToken());

        Assertions.assertNull(data);
    }

    @Test
    public void deleteAuthNonToken() throws DataAccessException {
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
    public void createGameDuplicate() throws DataAccessException {
        dataAccess.createGame(testGameData);

        Assertions.assertThrows(DataAccessException.class,
                () -> dataAccess.createGame(testGameData));
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
    public void clearGameSuccess() throws DataAccessException {}
}
