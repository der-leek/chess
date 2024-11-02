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

    GameData findGameData(Integer gameID) throws DataAccessException;

    void createGame(GameData data) throws DataAccessException;

    void updateGame(GameData data) throws DataAccessException;

    void clearUserDAO() throws DataAccessException;

    void clearAuthDAO() throws DataAccessException;

    void clearGameDAO() throws DataAccessException;

    boolean isUserDataEmpty() throws DataAccessException;

    boolean isAuthDataEmpty() throws DataAccessException;

    boolean isGameDataEmpty() throws DataAccessException;
}
