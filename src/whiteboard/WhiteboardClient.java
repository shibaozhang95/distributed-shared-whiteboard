package whiteboard;

import DataSource.ClientCommand;
import DataSource.User;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import javax.swing.*;
import java.io.*;
import java.net.Socket;

public class WhiteboardClient {
    private Socket socket;

    public WhiteboardClient(String username, String address, int port) throws IOException {
        User user = new User(username);
        socket = new Socket(address, port);
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        out.write(username);
        out.newLine();
        out.flush();
        System.out.println("Waiting for permission to join.");

        String permission = in.readLine();

        if (permission.equals("YES")) {
            PaintClient paintClient = new PaintClient(socket, user, false);
            paintClient.start();
            System.out.println("You are allowed to join.");
            out.write("");
            out.newLine();
            out.flush();
        }
        else if (permission.equals("OCCUPIED")) {
            JOptionPane.showMessageDialog(null, "This username is occupied by others.");
            System.exit(0);
        }
        else {
            JOptionPane.showMessageDialog(null, "You are refused to enter the whiteboard.");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        ClientCommand argsBean = new ClientCommand();

        CmdLineParser parser = new CmdLineParser(argsBean);

        try {
            parser.parseArgument(args);

            String address = argsBean.getHost();
            int port = argsBean.getPort();
            String username = argsBean.getUsername();

            // check it the username is valid
            if (username.matches("^[a-zA-Z0-9]+$")) {
                WhiteboardClient whiteboardClient = new WhiteboardClient(username, address, port);
            }
            else {
                System.out.println("Please provide a valid username. username can only be alphanumeric characters.");
            }

        } catch (CmdLineException e) {
            System.out.println("Error parsing parameters. Please run cml following the format below:\n");
            System.out.println("java -jar WhiteboardClient.jar -h <host-address> -p <port> -n <username>\n");
            System.out.println("<host-address>, <port> and <username> are required.\n");
        } catch (IOException e) {
            System.out.println("Cannot reach the server, please make sure the Host name and Port number are valid.");
        }

    }
}
