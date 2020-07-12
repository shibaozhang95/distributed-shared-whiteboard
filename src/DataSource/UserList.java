package DataSource;

import javax.swing.*;
import java.util.ArrayList;

public class UserList extends AbstractListModel {
    ArrayList<User> users = new ArrayList<User>();

    public UserList(ArrayList<User> users) {
        this.users = users;
        fireContentsChanged(this, 0, getSize());
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
        fireContentsChanged(this, 0, getSize());
    }

    @Override
    public int getSize() {
        return users.size();
    }

    @Override
    public Object getElementAt(int index) {
        return users.get(index);
    }

    public void add(User user) {
        if (users.add(user)) fireContentsChanged(this, 0, getSize());
    }


    public void removeUser(String name) {
        for (int i = 0; i < users.size(); ++i) {
            if (name.equals(users.get(i).getName())) {
                users.remove(users.get(i));
            }
        }

        fireContentsChanged(this, 0 , getSize());
    }
}
