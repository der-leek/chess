package dataaccess;

import model.*;
import java.util.HashMap;
import java.util.ArrayList;

public class MemoryDataAccess implements DataAccess {

    private HashMap<String, UserData> userData = new HashMap<>();
    private HashMap<String, AuthData> authData = new HashMap<>();
    private HashMap<Integer, GameData> gameData = new HashMap<>();

    public MemoryDataAccess() {}

    public UserData findUserData(String username) {
        return userData.get(username);
    }

    public void createUser(String username, UserData data) {
        userData.put(username, data);
    }

    public AuthData findAuthData(String authToken) {
        return authData.get(authToken);
    }

    public void createAuth(String authToken, AuthData data) {
        authData.put(authToken, data);
    }

    public ArrayList<GameData> listGames() {
        ArrayList<GameData> gamesList = new ArrayList<>();
        gamesList.addAll(gameData.values());
        return gamesList;
    }

    public GameData findGameData(Integer gameID) {
        return gameData.get(gameID);
    }

    public void createGame(Integer gameID, GameData data) {
        gameData.put(gameID, data);
    }

    public void updateGame(Integer gameID, GameData newGD) {
        createGame(gameID, newGD);
    }

    public void clearUserDAO() {
        userData.clear();
    }

    public void clearAuthDAO() {
        authData.clear();
    }

    public void clearGameDAO() {
        gameData.clear();
    }

    public boolean isUserDataEmpty() {
        return userData.isEmpty();
    }

    public boolean isAuthDataEmpty() {
        return authData.isEmpty();
    }

    public boolean isGameDataEmpty() {
        return gameData.isEmpty();
    }
}
