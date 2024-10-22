package service;

import model.*;
import dataaccess.*;
import requests.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UserServiceTests {

    private DataAccess dataAccess;
    private UserService userService;

    @BeforeEach
    public void setup() {
        dataAccess = new MemoryDataAccess();
        userService = new UserService(dataAccess);
    }

    @Test
    public void registerUserExists() {
        var request = new RegisterRequest("user", "pass", "email");
        dataAccess.createUser(request.username(),
                new UserData(request.username(), request.password(), request.email()));
        Assertions.assertThrows(DataAccessException.class, () -> userService.register(request));
    }

    @Test
    public void registerSuccessful() throws DataAccessException {
        var request = new RegisterRequest("user", "pass", "email");
        userService.register(request);
        var expectedUserData =
                new UserData(request.username(), request.password(), request.email());
        Assertions.assertEquals(expectedUserData, dataAccess.findUserData(request.username()));
    }

    @Test
    public void registerVerifyResult() throws DataAccessException {
        var request = new RegisterRequest("user", "pass", "email");
        var result = userService.register(request);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertInstanceOf(String.class, result.authToken());
        Assertions.assertEquals(result.username(), request.username());
    }

    @Test
    public void loginUserExists() {
        var request = new LoginRequest("us3r", "pass");

        String correctUsername = "user";
        dataAccess.createUser(correctUsername,
                new UserData(correctUsername, request.password(), "email"));

        Assertions.assertTrue(!dataAccess.isUserDataEmpty());
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(request));
    }

    @Test
    public void loginWrongPassword() {
        var request = new LoginRequest("user", "p4ss");

        String correctPassword = "pass";
        dataAccess.createUser(request.username(),
                new UserData(request.username(), correctPassword, "email"));

        Assertions.assertTrue(!dataAccess.isUserDataEmpty());
        Assertions.assertThrows(DataAccessException.class, () -> userService.login(request));
    }

    @Test
    public void loginSuccessful() throws DataAccessException {
        var request = new LoginRequest("user", "pass");
        dataAccess.createUser(request.username(),
                new UserData(request.username(), request.password(), "email"));
        Assertions.assertTrue(!dataAccess.isUserDataEmpty());

        var result = userService.login(request);
        Assertions.assertNotNull(result.authToken());
        Assertions.assertInstanceOf(String.class, result.authToken());
        Assertions.assertEquals(result.username(), request.username());
    }

    @Test
    public void logoutUnauthorized() throws AuthorizationException {
        var authToken = "auth";
        var authData = new AuthData(authToken, "user");
        dataAccess.createAuth(authToken, authData);
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        var request = new LogoutRequest("wrong" + authToken);
        Assertions.assertThrows(AuthorizationException.class, () -> userService.logout(request));
    }

    @Test
    public void logoutSuccessful() throws AuthorizationException {
        var authToken = "auth";
        var authData = new AuthData(authToken, "user");
        dataAccess.createAuth(authToken, authData);
        Assertions.assertTrue(!dataAccess.isAuthDataEmpty());

        var request = new LogoutRequest(authToken);
        userService.logout(request);
        Assertions.assertTrue(dataAccess.isAuthDataEmpty());
    }
}
