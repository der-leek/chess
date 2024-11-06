import java.util.Scanner;

public class Repl {
    private Scanner scanner;
    private boolean LOGGED_IN;

    public Repl(Scanner scanner) {
        this.scanner = scanner;
        LOGGED_IN = false;
    }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            Repl repl = new Repl(scanner);
            repl.printMenuNotLogin();
            repl.runMenuNotLogin();
        }
    }

    private void printMenuNotLogin() {
        System.out.printf("%nWelcome to this chess experience! Enter a number to proceed:%n");
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("3: Help");
        System.out.printf("4: Quit%n>>> ");
    }

    private void runMenuNotLogin() {
        while (!LOGGED_IN) {
            String line = scanner.nextLine().trim();
            System.out.println();

            switch (line) {
                case ("register"):
                case ("Register"):
                case ("1"):
                    register();
                    break;

                case ("login"):
                case ("Login"):
                case ("2"):
                    login();
                    break;

                case ("help"):
                case ("Help"):
                case ("3"):
                    help();
                    break;

                case ("quit"):
                case ("Quit"):
                case ("4"):
                    System.exit(0);
                    break;

                default:
                    printMenuNotLogin();
                    break;
            }
        }
    }

    private void register() {
        System.out.printf("Choose a username: ");
        String username = scanner.nextLine().trim();
        System.out.printf("Choose a password: ");
        String password = scanner.nextLine().trim();
        System.out.printf("Enter your email: ");
        String email = scanner.nextLine().trim();
        LOGGED_IN = true;
    }

    private void login() {
        System.out.printf("Username: ");
        String username = scanner.nextLine().trim();
        System.out.printf("Password: ");
        String password = scanner.nextLine().trim();
        LOGGED_IN = true;
    }

    private void help() {
        System.out.println("1: Register an account with username, password, email");
        System.out.println("2: Login to an existing account with username, password");
        System.out.println("3: Display this message again");
        System.out.printf("4: Exit the application%n>>> ");
    }
}

