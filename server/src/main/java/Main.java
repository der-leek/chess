import server.*;

public class Main {
    public static void main(String[] args) {
        int port = 8080;
        try {
            Server server = new Server();
            server.run(port);
        } catch (RuntimeException e) {
            System.out.println("Verify that DB is running: " + e.getMessage());
        }
    }
}
