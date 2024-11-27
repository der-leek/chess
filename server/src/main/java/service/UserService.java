package service;

import model.*;
import dataaccess.*;
import requests.*;
import responses.*;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;
import com.google.gson.JsonSyntaxException;

public class UserService {

    private DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public LoginResponse register(RegisterRequest req)
            throws DataAccessException, JsonSyntaxException, AuthorizationException {
        String username = req.username();

        if (req.password() == null) {
            throw new JsonSyntaxException("must submit password");
        }

        if (dataAccess.findUserData(username) != null) {
            throw new DataAccessException(username + " already taken. Choose another username");
        }

        UserData usr = new UserData(username, req.password(), req.email());
        dataAccess.createUser(usr);

        AuthData authData = createAuth(username);
        return new LoginResponse(username, authData.authToken());
    }

    public LoginResponse login(LoginRequest req)
            throws DataAccessException, AuthorizationException {
        String username = req.username();
        var userData = dataAccess.findUserData(username);

        if (userData == null) {
            throw new AuthorizationException("Invalid username: " + username);
        }

        if (!BCrypt.checkpw(req.password(), userData.password())) {
            throw new AuthorizationException("Invalid password");
        }

        AuthData authData = createAuth(username);
        return new LoginResponse(username, authData.authToken());
    }

    public void logout(LogoutRequest req) throws AuthorizationException, DataAccessException {
        var authData = dataAccess.findAuthData(req.authToken());

        if (authData == null) {
            throw new AuthorizationException("Unauthorized");
        }

        dataAccess.deleteAuth(authData.authToken());
    }

    private AuthData createAuth(String username)
            throws AuthorizationException, DataAccessException {
        String authToken = UUID.randomUUID().toString();
        var authData = new AuthData(authToken, username);
        dataAccess.createAuth(authData);
        return authData;
    }
}
