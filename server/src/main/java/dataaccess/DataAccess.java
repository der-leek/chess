package dataaccess;

import model.*;
import service.AuthorizationException;
import java.util.ArrayList;

public interface DataAccess {
    UserData findUserData(String username) throws AuthorizationException, DataAccessException;

    void createUser(UserData data) throws AuthorizationException, DataAccessException;

    AuthData findAuthData(String authToken) throws AuthorizationException, DataAccessException;

    void createAuth(AuthData data) throws AuthorizationException, DataAccessException;

    void deleteAuth(String authToken) throws AuthorizationException, DataAccessException;

    ArrayList<GameData> listGames() throws DataAccessException;

    GameData findGameData(Integer gameID) throws DataAccessException;

    void createGame(GameData data) throws AuthorizationException, DataAccessException;

    void updateGame(GameData data) throws AuthorizationException, DataAccessException;

    void clearUserDAO() throws DataAccessException;

    void clearAuthDAO() throws DataAccessException;

    void clearGameDAO() throws DataAccessException;

    boolean isUserDataEmpty() throws DataAccessException;

    boolean isAuthDataEmpty() throws DataAccessException;

    boolean isGameDataEmpty() throws DataAccessException;
}
