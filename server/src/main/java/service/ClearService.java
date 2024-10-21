package service;

import dataaccess.*;

public class ClearService {

    private DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clearUsers() {
        dataAccess.clearUserDAO();
    }

    public void clearAuths() {
        dataAccess.clearAuthDAO();
    }

    public void clearGames() {
        dataAccess.clearGameDAO();
    }
}
