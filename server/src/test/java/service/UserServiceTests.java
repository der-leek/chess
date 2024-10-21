package service;

import dataaccess.*;
import model.*;
import requests_responses.RegisterRequest;
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
}
