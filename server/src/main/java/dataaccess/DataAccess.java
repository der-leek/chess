package dataaccess;

import model.*;
import java.util.ArrayList;

public interface DataAccess {
    UserData findUserData(String username) throws DataAccessException;

    void createUser(UserData data) throws DataAccessException;

    AuthData findAuthData(String authToken) throws DataAccessException;

    void createAuth(AuthData data) throws DataAccessException;

    void deleteAuth(String authToken) throws DataAccessException;

    ArrayList<GameData> listGames();

    GameData findGameData(Integer gameID);

    void createGame(GameData data);

    void updateGame(GameData data);

    void clearUserDAO() throws DataAccessException;

    void clearAuthDAO() throws DataAccessException;

    void clearGameDAO();

    boolean isUserDataEmpty() throws DataAccessException;

    boolean isAuthDataEmpty() throws DataAccessException;

    boolean isGameDataEmpty() throws DataAccessException;
}
