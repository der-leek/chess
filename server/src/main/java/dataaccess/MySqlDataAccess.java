package dataaccess;

import model.*;
import java.sql.*;
import serializer.*;
import chess.ChessGame;
import java.util.ArrayList;
import org.mindrot.jbcrypt.BCrypt;
import service.AuthorizationException;
import com.google.gson.JsonSyntaxException;

public class MySqlDataAccess implements DataAccess {

    private final Serializer serializer = new Serializer();
    private final String[] createStatements = {"""
            CREATE TABLE IF NOT EXISTS userdata (
                username VARCHAR(255) NOT NULL PRIMARY KEY,
                password VARCHAR(255) NOT NULL,
                email VARCHAR(255) NOT NULL
            )
            """, """
            CREATE TABLE IF NOT EXISTS authdata (
                authToken VARCHAR(255) NOT NULL,
                username VARCHAR(255) NOT NULL,
                foreign key(username) references userdata(username),
                UNIQUE (authToken)
            )
            """, """
            CREATE TABLE IF NOT EXISTS gamedata (
                id INT NOT NULL,
                whiteUsername VARCHAR(255),
                blackUsername VARCHAR(255),
                gameName VARCHAR(255) NOT NULL,
                game longtext NOT NULL,
                foreign key(whiteUsername) references userdata(username),
                foreign key(blackUsername) references userdata(username),
                UNIQUE (id)
            )
            """};

    public MySqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    public UserData findUserData(String username)
            throws AuthorizationException, DataAccessException {
        sanitizeUsername(username);

        String query = "SELECT * FROM userdata WHERE username=?";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, username);
            try (var rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return new UserData(rs.getString("username"), rs.getString("password"),
                        rs.getString("email"));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void createUser(UserData data) throws AuthorizationException, DataAccessException {
        sanitizeUsername(data.username());
        sanitizePassword(data.password());
        sanitizeEmail(data.email());

        String statement = "REPLACE INTO userdata (username, password, email) VALUES (?, ?, ?)";
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

    public AuthData findAuthData(String authToken)
            throws AuthorizationException, DataAccessException {
        sanitizeAuthToken(authToken);

        String query = "SELECT * FROM authdata WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setString(1, authToken);
            try (var rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                return new AuthData(rs.getString("authToken"), rs.getString("username"));
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void createAuth(AuthData data) throws AuthorizationException, DataAccessException {
        sanitizeAuthToken(data.authToken());
        sanitizeUsername(data.username());

        String statement = "REPLACE INTO authdata (authToken, username) VALUES (?, ?)";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, data.authToken());
            preparedStatement.setString(2, data.username());
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void deleteAuth(String authToken) throws AuthorizationException, DataAccessException {
        sanitizeAuthToken(authToken);

        String statement = "DELETE FROM authdata WHERE authToken=?";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.setString(1, authToken);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public ArrayList<GameData> listGames() throws DataAccessException {
        ArrayList<GameData> games = new ArrayList<GameData>();
        String query = "SELECT * FROM gamedata";

        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(query)) {
            try (var rs = preparedStatement.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String whiteUsername = rs.getString("whiteUsername");
                    String blackUsername = rs.getString("blackUsername");
                    String gameName = rs.getString("gameName");
                    ChessGame game = serializer.fromJson(rs.getString("game"), ChessGame.class);

                    games.add(new GameData(id, whiteUsername, blackUsername, gameName, game));
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
        return games;
    }

    public GameData findGameData(Integer gameID) throws DataAccessException {
        String query = "SELECT * FROM gamedata WHERE id=?";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(query)) {

            preparedStatement.setInt(1, gameID);
            try (var rs = preparedStatement.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }

                int foundGameID = rs.getInt("id");
                String whiteUsername = rs.getString("whiteUsername");
                String blackUsername = rs.getString("blackUsername");
                String gameName = rs.getString("gameName");
                ChessGame game = serializer.fromJson(rs.getString("game"), ChessGame.class);

                return new GameData(foundGameID, whiteUsername, blackUsername, gameName, game);
            }
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void createGame(GameData data) throws AuthorizationException, DataAccessException {
        String game = sanitizeGameData(data);

        String statement =
                "INSERT INTO gamedata (id, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?)";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            setCreateStatement(data, game, preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void updateGame(GameData data) throws AuthorizationException, DataAccessException {
        String game = sanitizeGameData(data);

        String statement =
                "UPDATE gamedata SET whiteUsername=?, blackUsername=?, gameName=?, game=? where id=?";
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            setUpdateStatement(data, game, preparedStatement);
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
    }

    public void clearUserDAO() throws DataAccessException {
        clearDAO("userdata");
    }

    public void clearAuthDAO() throws DataAccessException {
        clearDAO("authdata");
    }

    public void clearGameDAO() throws DataAccessException {
        clearDAO("gamedata");
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

    private void sanitizeUsername(String username) throws AuthorizationException {
        if (!username.matches("[\\w\\d\\.-]+")) {
            throw new AuthorizationException("Invalid username. No special characters allowed");
        }
    }

    private void sanitizePassword(String password) throws AuthorizationException {
        if (!password.matches("[\\w\\d\\.!-]+")) {
            throw new AuthorizationException("Invalid password. No special characters allowed");
        }
    }

    private void sanitizeEmail(String email) throws AuthorizationException {
        if (!email.matches("^[\\w\\d\\.@-]+$")) {
            throw new AuthorizationException("Invalid email.");
        }
    }

    private void sanitizeAuthToken(String authToken) throws AuthorizationException {
        if (!authToken.matches(
                "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$")) {
            throw new AuthorizationException("Invalid authToken");
        }
    }

    private void setCreateStatement(GameData data, String game, PreparedStatement preparedStatement)
            throws SQLException {
        preparedStatement.setInt(1, data.gameID());
        preparedStatement.setString(2, data.whiteUsername());
        preparedStatement.setString(3, data.blackUsername());
        preparedStatement.setString(4, data.gameName());
        preparedStatement.setString(5, game);
    }

    private String sanitizeGameData(GameData data)
            throws AuthorizationException, DataAccessException {
        boolean cleanGameID = (Object) data.gameID() instanceof Integer;
        boolean nullWhiteUsername = data.whiteUsername() == null;
        boolean nullBlackUsername = data.blackUsername() == null;
        boolean cleanGameName = data.gameName().matches("[^;]+");

        if (!nullWhiteUsername) {
            sanitizeUsername(data.whiteUsername());
        } else if (!nullBlackUsername) {
            sanitizeUsername(data.blackUsername());
        } else if (!cleanGameID || !cleanGameName) {
            throw new DataAccessException("Invalid gameData");
        }

        try {
            return serializer.toJson(data.game());
        } catch (JsonSyntaxException ex) {
            throw new DataAccessException("Invalid ChessGame Object");
        }
    }

    private void setUpdateStatement(GameData data, String game, PreparedStatement preparedStatement)
            throws SQLException {
        preparedStatement.setString(1, data.whiteUsername());
        preparedStatement.setString(2, data.blackUsername());
        preparedStatement.setString(3, data.gameName());
        preparedStatement.setString(4, game);
        preparedStatement.setInt(5, data.gameID());
    }

    private void clearDAO(String dbTable) throws DataAccessException {
        String statement = "DELETE FROM " + dbTable;
        try (var conn = DatabaseManager.getConnection();
                var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException(ex.getMessage());
        }
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
