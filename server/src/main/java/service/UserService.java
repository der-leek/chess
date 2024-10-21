package service;

import model.*;
import dataaccess.*;
import requests_responses.*;
import java.util.UUID;

public class UserService {

    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public RegisterResponse register(RegisterRequest req) throws DataAccessException {
        String username = req.username();

        if (dataAccess.findUserData(username) != null) {
            throw new DataAccessException(username + " already taken. Choose another username");
        }

        UserData usr = new UserData(username, req.password(), req.email());
        dataAccess.createUser(username, usr);

        String authToken = UUID.randomUUID().toString();
        var authData = new AuthData(authToken, username);
        dataAccess.createAuth(authToken, authData);

        return new RegisterResponse(username, authToken);
    }
}
