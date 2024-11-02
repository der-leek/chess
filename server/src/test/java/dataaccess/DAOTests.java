package dataaccess;

import model.*;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;

public class DAOTests {

    private DataAccess dataAccess;

    public DAOTests() throws DataAccessException {
        dataAccess = new MySqlDataAccess();
    }

    @BeforeEach
    public void setup() throws DataAccessException {
        dataAccess.clearGameDAO();
        dataAccess.clearAuthDAO();
        dataAccess.clearUserDAO();
    }

    @Test
    public void createNewUserSuccess() throws DataAccessException {
        String username = "user";
        String password = "pass";
        String email = "email@email.com";

        dataAccess.createUser(new UserData(username, password, email));
        var data = dataAccess.findUserData(username);

        Assertions.assertEquals(data.username(), username);
        Assertions.assertEquals(data.email(), email);
        Assertions.assertTrue(BCrypt.checkpw(password, data.password()));
    }

    @Test
    public void createNewUserDuplicateUsername() throws DataAccessException {
        String username = "user";
        String password = "pass";
        String email = "email@email.com";

        dataAccess.createUser(new UserData(username, password, email));
        Assertions.assertThrows(DataAccessException.class,
                () -> dataAccess.createUser(new UserData(username, password, email)));
    }

    @Test
    public void clearUserSuccess() throws DataAccessException {}

    @Test
    public void clearAuthSuccess() throws DataAccessException {}

    @Test
    public void clearGameSuccess() throws DataAccessException {}
}
