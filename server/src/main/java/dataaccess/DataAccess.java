package dataaccess;

import model.*;
import java.util.ArrayList;

public interface DataAccess {
    UserData findUserData(String username);

    void createUser(UserData data) throws DataAccessException;

    AuthData findAuthData(String authToken);

    void createAuth(AuthData data);

    void deleteAuth(String authToken);

    ArrayList<GameData> listGames();

    GameData findGameData(Integer gameID);

    void createGame(GameData data);

    void updateGame(GameData data);

    void clearUserDAO();

    void clearAuthDAO();

    void clearGameDAO();

    boolean isUserDataEmpty();

    boolean isAuthDataEmpty();

    boolean isGameDataEmpty();
}
