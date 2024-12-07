package ui;

import java.util.Map;
import java.util.Scanner;
import serializer.Serializer;
import server.ServerFacade;

public class PreLoginClient {

    private String user;
    private String authToken;
    private final Scanner scanner;
    private final ServerFacade serverFacade;
    private final Serializer serializer = new Serializer();

    public PreLoginClient(Scanner scanner, ServerFacade serverFacade) throws Exception {
        this.scanner = scanner;
        this.serverFacade = serverFacade;
        runMenu();
    }

    public Login runMenu() {
        System.out.println();
        printMenu();
        String line = scanner.nextLine().trim();
        System.out.println();

        switch (line) {
            case "1" -> register();
            case "2" -> login();
            case "3" -> help();
            case "4" -> System.exit(0);
            case "clear" -> clearDB();
        }

        return new Login(user, authToken);
    }

    private void printMenu() {
        System.out.println("Enter a number to proceed:");
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("3: Help");
        System.out.print("4: Quit\n>>> ");
    }

    private void clearDB() {
        var result = serverFacade.clear();
        if (result == null) {
            MessagePrinter.printBoldItalic("Remember to start the server!");
            return;
        }

        if (result.get("statusCode").equals("200")) {
            MessagePrinter.printBoldItalic("DB cleared!");
        }

        user = null;
        authToken = null;
    }

    private void register() {
        chooseUsername();
        String password = chooseEmail();
        String email = choosePassword();

        var response = serverFacade.register(user, password, email);

        if (response == null) {
            MessagePrinter.printBoldItalic("There was an error registering. Please try again.");
            return;
        }

        if (!response.get("statusCode").equals("200")) {
            MessagePrinter.printBoldItalic("Invalid username. Please try another.");
            return;
        }

        getAuthToken(response);
    }

    private void getAuthToken(Map<String, String> response) {
        var body = serializer.fromJson(response.get("body"), Map.class);
        authToken = (String) body.get("authToken");
    }

    private String choosePassword() {
        System.out.print("Enter your email: ");
        String email = scanner.nextLine().trim();
        return email;
    }

    private String chooseEmail() {
        System.out.print("Choose a password: ");
        String password = scanner.nextLine().trim();
        return password;
    }

    private void chooseUsername() {
        System.out.print("Choose a username: ");
        user = scanner.nextLine().trim();
    }

    private void login() {
        getUsername();
        String password = getPassword();
        var response = serverFacade.login(user, password);
        String tryAgain = "There was an error logging in. Please try again.";

        if (response == null) {
            MessagePrinter.printBoldItalic(tryAgain);
            return;
        }

        var statusCode = response.get("statusCode");
        if (statusCode.equals("500")) {
            MessagePrinter.printBoldItalic(tryAgain);
            return;
        }

        if (statusCode.equals("401")) {
            MessagePrinter.printBoldItalic("Invalid credentials. Please try again.");
            return;
        }

        getAuthToken(response);
    }

    private void getUsername() {
        System.out.print("Username: ");
        user = scanner.nextLine().trim();
    }

    private String getPassword() {
        System.out.print("Password: ");
        String password = scanner.nextLine().trim();
        return password;
    }

    private void help() {
        System.out.print(EscapeSequences.SET_BOLD_ITALIC);

        System.out.println("1: Register an account with <USERNAME>, <PASSWORD>, <EMAIL@MAIL.COM>");
        System.out.println("2: Login to an existing account with <USERNAME>, <PASSWORD>");
        System.out.println("3: Display this message again");
        System.out.println("4: Exit CS240 Chess");

        System.out.print(EscapeSequences.RESET_BOLD_ITALIC);
    }
}
