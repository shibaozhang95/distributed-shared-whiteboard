package whiteboard;

import DataSource.*;
import DataSource.DataSourceList;
import DataSource.User;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class MultiThead extends Thread {
    private Socket client;
    private BufferedReader in;
    private BufferedWriter out;
    private User user;
    private WhiteboardService server;

    public MultiThead(Socket socket, WhiteboardService server) throws IOException {
        this.client = socket;
        this.server = server;

        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        String name = in.readLine();

        SubscriberList subscriberList = server.userList;
        DataSourceList dataList = server.dataList;

        // handle with the same name
        if (subscriberList.isNameOccupied(name)) {
            out.write("OCCUPIED");
            out.newLine();
            out.flush();
            return;
        }

        if (!name.equals("MANAGER")) {

            int response = JOptionPane.showConfirmDialog(null, "Do you want " + name + " to join?",
                    "permission", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (response == JOptionPane.NO_OPTION) {
                out.write("NO");
                out.newLine();
                out.flush();
                return;
            }
            else if (response == JOptionPane.YES_OPTION) {
                out.write("YES");
                out.newLine();
                out.flush();
            }
        }


        user = new User(name, socket);

        // make sure the user is working
        in.readLine();
        // INIT DRAWing
        if (dataList.list.size() > 0) {
            out.write("INIT:" + dataList.toString());
            out.newLine();
            out.flush();
        }

        // INIT USER LIST
        if (subscriberList.getList().size() > 0) {
            out.write("INIT_USERS:" + subscriberList.toString());
            out.newLine();
            out.flush();
        }
    }

    public void run() {
        try {
            server.addUser(user);
            handleRequest();
        } catch (IOException e) {
        } finally {
            try {
                server.removeUser(user);
            } catch (IOException e) {
            }
        }
    }

    public void handleRequest() throws IOException {
        while (true) {
            String dataBuffer = in.readLine();

            String[] params = dataBuffer.split(":");
            if (params[0].equals("DRAW")) {

                server.addData(new DataSource(params[1]));
            }
            else if (params[0].equals("INIT")) {
                server.resetDataList(params.length > 1 ? params[1] : "");
            }

            server.userList.update(dataBuffer);
        }
    }
}
