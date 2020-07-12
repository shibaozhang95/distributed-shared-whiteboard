package DataSource;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class SubscriberList {
    ArrayList<User> list;

    public SubscriberList() {
        list = new ArrayList<User>();
    }

    public synchronized void append(User user) throws IOException {
        list.add(user);

        update("ADD_USER:" + user.getName());
    }

    public synchronized void remove(User user) throws IOException {
        list.remove(user);

        update("REMOVE_USER:" + user.getName());
    }

    public ArrayList<User> getList() {
        return list;
    }

    public boolean isNameOccupied(String username) {
        for (int i = 0; i < list.size(); ++i) {
            if (username.equals(list.get(i).getName())) {
                return true;
            }
        }
        return false;
    }

    public String toString() {
        ArrayList<String> names = new ArrayList<String>();

        for (int i = 0; i < list.size(); ++i) {
            names.add(list.get(i).getName());
        }

        return String.join(";", names);
    }

    public synchronized void update(String dataBuffer) throws IOException {
        for (int i = 0; i < list.size(); ++i) {
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                    list.get(i).getClient().getOutputStream()
            ));

            out.write(dataBuffer);
            out.newLine();
            out.flush();
        }
    }
}
