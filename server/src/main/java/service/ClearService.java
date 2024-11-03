package service;

import dataaccess.*;

public class ClearService {

    private DataAccess dataAccess;

    public ClearService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clearUsers() throws DataAccessException {
        dataAccess.clearUserDAO();
    }

    public void clearAuths() throws DataAccessException {
        dataAccess.clearAuthDAO();
    }

    public void clearGames() throws DataAccessException {
        dataAccess.clearGameDAO();
    }
}
