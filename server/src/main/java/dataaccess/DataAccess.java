package dataaccess;

import model.*;
import java.util.ArrayList;

public interface DataAccess {
    UserData findUserData(String username);

    void createUser(String username, UserData data);

    AuthData findAuthData(String authToken);

    void createAuth(String authToken, AuthData data);

    void deleteAuth(String authToken);

    ArrayList<GameData> listGames();

    GameData findGameData(Integer gameID);

    void createGame(Integer gameID, GameData data);

    void updateGame(Integer gameID, GameData data);

    void clearUserDAO();

    void clearAuthDAO();

    void clearGameDAO();

    boolean isUserDataEmpty();

    boolean isAuthDataEmpty();

    boolean isGameDataEmpty();
}
