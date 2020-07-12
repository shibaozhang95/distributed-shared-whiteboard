package whiteboard;

import DataSource.*;
import DataSource.SubscriberList;
import DataSource.User;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class WhiteboardService extends Thread {
    public ServerSocket serverSocket = null;

    public SubscriberList userList;
    public DataSourceList dataList;

    public WhiteboardService(int port) throws IOException {
        serverSocket = new ServerSocket(port);
        userList = new SubscriberList();
        dataList = new DataSourceList();
    }

    public void run() {
        try {
            handleConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleConnection() throws IOException {
        while (true) {
            Socket clientSocket = null;
            clientSocket = serverSocket.accept();
            System.out.println("Connected");
            MultiThead clientThread = new MultiThead(clientSocket,this);
            clientThread.start();
        }
    }

    public void addUser(User user) throws IOException {
        synchronized (userList) {
            userList.append(user);
        }
    }

    public void removeUser(User user) throws IOException {
        synchronized (userList) {
            userList.remove(user);
        }
    }

    public void addData(DataSource data) {
        synchronized (dataList) {
            dataList.addData(data);
        }
    }

    public void resetDataList(String initStr) {
        synchronized (dataList) {
            dataList.resetData(initStr);
        }
    }

    public static void main(String[] args)  {
        ServerCommand argsBean = new ServerCommand();
        CmdLineParser parser = new CmdLineParser(argsBean);

        try {
            parser.parseArgument(args);

            int port = argsBean.getPort();
            WhiteboardService whiteboardService = new WhiteboardService(port);
            whiteboardService.start();

            // Manager
            User manager = new User(true);
            Socket managerSocket = new Socket("localhost", port);

            // register request
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(managerSocket.getOutputStream()));
            out.write("MANAGER");
            out.newLine();
            out.flush();

            // start to work register
            PaintClient paintClient = new PaintClient(managerSocket, manager, true);
            paintClient.start();
            out.write("");
            out.newLine();
            out.flush();

        } catch (CmdLineException e) {
            System.out.println("Error parsing parameters. Please run cml following the format below:\n");
            System.out.println("java -jar WhiteboardService.jar -p <port>\n");
            System.out.println("<port> is required.\n");
        } catch (IOException e) {
            System.out.println("This port is occupied. Please provide a valid port number.");
        }
    }
}
