package dataaccess;

import model.*;
import java.sql.*;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;

public class MySqlDataAccess implements DataAccess {

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public UserData findUserData(String username) throws DataAccessException {
        return null;
    }

    public void createUser(UserData data) throws DataAccessException {
        boolean cleanUsername = data.username().matches("[a-zA-Z]+");
        boolean cleanPassword = data.password().matches("[a-zA-Z]+");
        boolean cleanEmail = data.email().matches("^[\\w\\.-]+@[a-zA-Z\\d\\.-]+\\.[a-zA-Z]{2,}$");

        if (!cleanUsername || !cleanPassword || !cleanEmail) {
            throw new DataAccessException("Invalid username. No special characters allowed");
        }

        String statement = "INSERT INTO userdata (username, password, email) VALUES (?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            String hashedPassword = BCrypt.hashpw(data.password(), BCrypt.gensalt());
            preparedStatement.setString(1, data.username());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, data.email());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public AuthData findAuthData(String authToken) throws DataAccessException {
        return null;
    }

    public void createAuth(AuthData data) throws DataAccessException {}

    public void deleteAuth(String authToken) throws DataAccessException {}

    public ArrayList<GameData> listGames() {
        return null;
    }

    public GameData findGameData(Integer gameID) {
        return null;
    }

    public void createGame(GameData data) {}

    public void updateGame(GameData data) {}

    public void clearUserDAO() throws DataAccessException {
        String statement = "DELETE FROM userdata";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void clearAuthDAO() throws DataAccessException {
        String statement = "DELETE FROM authdata";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void clearGameDAO() throws DataAccessException {
        String statement = "DELETE FROM gamedata";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public boolean isUserDataEmpty() throws DataAccessException {
        return isDataEmpty("userdata");
    }

    public boolean isAuthDataEmpty() throws DataAccessException {
        return isDataEmpty("authdata");
    }

    public boolean isGameDataEmpty() throws DataAccessException {
        return isDataEmpty("gamedata");
    }

    private boolean isDataEmpty(String table) throws DataAccessException {
        if (!table.matches("[a-zA-Z]+")) {
            throw new DataAccessException("Invalid table name");
        }

        String query = "SELECT * FROM " + table;
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(query)) {

            try (var rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    return true;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        return false;
    }

    private final String[] createStatements = {"""
            CREATE TABLE IF NOT EXISTS userdata (
                username VARCHAR(255) NOT NULL primary key,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL,
                PRIMARY KEY (username)
            )
            """, """
            CREATE TABLE IF NOT EXISTS authdata (
                username VARCHAR(255) NOT NULL,
                authToken VARCHAR(255) NOT NULL,
                foreign key(username) references userdata(username),
                UNIQUE (authToken)
            )
            """, """
            CREATE TABLE IF NOT EXISTS gamedata (
                id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255),
                game VARCHAR(1023), -- look at how large a serialized chess game is
                foreign key(whiteUsername) references userdata(username),
                foreign key(blackUsername) references userdata(username)
            )
            """};

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(
                    String.format("Unable to configure database: %s", ex.getMessage()));
        }
    }
}


