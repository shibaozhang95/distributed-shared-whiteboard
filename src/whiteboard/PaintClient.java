package whiteboard;

import DataSource.*;
import GUI.PaintCanvas;
import GUI.WhiteboardFrame;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class PaintClient extends Thread {
    private User self;
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private PaintCanvas canvas;
    private UserList userList;


    public PaintClient(Socket socket, User user, Boolean isManager) throws IOException {
        this.socket = socket;
        this.self = user;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        WhiteboardFrame app = new WhiteboardFrame(socket, user, isManager);
        this.canvas = app.getCanvas();
        this.userList = app.getUserListModel();

        app.setVisible(true);
    }

    public void run() {
        try {
            readMessage();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "You are disconnected from the server.");
            System.exit(0);
        }
    }

    public void readMessage() throws IOException {
        while (true) {
            String dataBuffer = in.readLine();

            String[] params = dataBuffer.split(":");

            switch (params[0]) {
                case "DRAW":
                    handleDrawMessage(params[1]);
                    break;
                case "INIT":
                    handleInitialisation(params.length > 1 ? params[1] : "");
                    break;
                case "CLEAR":
                   handleClear();
                   break;
                case "INIT_USERS":
                    handleUsersInitialisation(params[1]);
                    break;
                case "ADD_USER":
                    handleAddUser(params[1]);
                    break;
                case "REMOVE_USER":
                    handleRemoveUser(params[1]);
                    break;
            }

        }
    }

    public void handleDrawMessage(String params) {
        DataSource data = new DataSource(params);

        if (data.getUsername().equals(self.getName())) return;

        canvas.getDataList().addData(data);
        canvas.repaint();
    }

    public void handleInitialisation(String params) {
        DataSourceList initList = new DataSourceList(params);

        canvas.setDataList(initList);
        canvas.repaint();
    }

    public void handleClear() {
        DataSourceList emptyList = new DataSourceList();
        canvas.setDataList(emptyList);
        canvas.repaint();
    }

    public void handleUsersInitialisation(String params) {
        String[] names = params.split(";");

        ArrayList<User> users = new ArrayList<User>();

        for (int i = 0; i < names.length; ++i) {
            User user = new User(names[i]);
            users.add(user);
        }

        userList.setUsers(users);
    }

    public void handleAddUser(String username) {
        User user = new User(username);
        userList.add(user);
    }

    public void handleRemoveUser(String username) {
        if (self.getName().equals(username)) {
            JOptionPane.showMessageDialog(null,
                    "You have been kicked out of the whiteboard by manager.");
            System.exit(0);
        }
        userList.removeUser(username);
    }
}
