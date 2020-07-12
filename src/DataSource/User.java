package DataSource;

import java.net.Socket;

public class User {
    private String name = "Guest";
    private boolean isManager;
    private Socket client;

    public User(String username) {
        name = username;
        this.client = null;
    }

    public User(String username, Socket client) {
        name = username;
        this.client = client;
    }

    public User(boolean isManager) {
        this.isManager = isManager;
        this.name = "Manager";
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return this.name;
    }

    public Socket getClient() {
        return client;
    }
}
