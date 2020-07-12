package GUI;

import DataSource.DataSourceList;
import DataSource.User;
import DataSource.UserList;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class WhiteboardFrame extends JFrame implements ActionListener {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private Container c = getContentPane();

    private JMenuItem jMenuItem[][] = new JMenuItem[1][5];

    // for Save and Save as
    private String fileName = "";
    private String filePath = "";

    private int defaultTool = DrawType.LINE;
    private PaintCanvas drawPanel;

    private ArrayList<User> users = new ArrayList<User>();
    private UserList userListModel = new UserList(users);

    private User self;
    private boolean isManager;

    @Override
    public void actionPerformed(ActionEvent e) {
        // new
        if (e.getSource() == jMenuItem[0][0]) {
            filePath = "";
            fileName = "";
            drawPanel.clear();
        }
        // open
        else if (e.getSource() == jMenuItem[0][1]) {
            open();
        }
        // save
        else if (e.getSource() == jMenuItem[0][2]) {
            if ("".equals(filePath + fileName)) {
                saveAs();
            }
            else {
                save();
            }
        }
        // save as
        else if (e.getSource() == jMenuItem[0][3]) {
            saveAs();
        }
        // close
        else if (e.getSource() == jMenuItem[0][4]) {
            close();
        }
    }

    public WhiteboardFrame(Socket socket, User user, Boolean isManager) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        this.self = user;
        this.isManager = isManager;

        this.setDefault();
        try {
            this.setLayout();
        } catch (IOException e) {
            System.out.println("Something wrong when setting up the UI.");
        }
        this.setVisible(true);
    }

    public PaintCanvas getCanvas() {
        return drawPanel;
    }

    public UserList getUserListModel() {
        return userListModel;
    }

    private void setDefault() {
        setTitle("Whiteboard - " + self.getName());

        this.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int response = JOptionPane.showConfirmDialog(null,
                        "Do you want to quit?", "Confirm",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.CANCEL_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });

        this.defaultTool = DrawType.LINE;
    }

    private void setMenu() {
        // menu areas
        String menuBar[] = { "File" };
        String menuItem[][] = {
                {"New", "Open", "Save", "Save as", "Close"}
        };
        JMenu jMenu[];

        JMenuBar bar = new JMenuBar();
        jMenu = new JMenu[menuBar.length];

        // add menu
        for (int i = 0; i < menuBar.length; ++i) {
            jMenu[i] = new JMenu(menuBar[i]);
            bar.add(jMenu[i]);

            // add menu item
            for (int j = 0; j < menuItem[i].length; ++j) {
                jMenu[i].addSeparator();
                jMenuItem[i][j] = new JMenuItem(menuItem[i][j]);
                jMenuItem[i][j].addActionListener(this);
                jMenu[i].add(jMenuItem[i][j]);
            }
        }
        this.setJMenuBar(bar);
    }

    public void setLayout() throws IOException {
        // only Manager has menu
        if (isManager) {
            this.setMenu();
        }

        // window size
        this.setSize(900, 650);
        this.setLocationRelativeTo(null);

        JPanel toolBoxPanel = new JPanel();
        toolBoxPanel.add(initialiseToolBox());

        JPanel bigDrawPanel = new JPanel();
        bigDrawPanel.add(initialiseDrawPanel(600, 500));

        JPanel userListPanel = initialiseUserList(200, 400);

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(null);
        toolBoxPanel.setBounds(0, 0, 600, 40);
        bigDrawPanel.setBounds(0, 40, 600, 500);
        leftPanel.add(toolBoxPanel);
        leftPanel.add(bigDrawPanel);

        c.setLayout(null);
        c.add(userListPanel);
        c.add(leftPanel);
        leftPanel.setBounds(50, 0, 600, 600);
        userListPanel.setBounds(700, 0, 150, 600);

        this.setVisible(true);
    }

    private JToolBar initialiseToolBox() {
        String toolButtonName[] = {
                "Line", "Circle", "Rectangle", "Text"
        };
        JToggleButton toggleButton[];
        Icon toolIcons[] = new ImageIcon[4];
        String toolIconPath[] = {
                "src/statics/icon-line.png",
                "src/statics/icon-circle.png",
                "src/statics/icon-rectangle.png",
                "src/statics/icon-text.png"
        };

        ButtonGroup buttonGroup = new ButtonGroup();

        JToolBar jToolBar = new JToolBar("Tools");

        toggleButton = new JToggleButton[toolButtonName.length];
        for (int i = 0; i < toolButtonName.length; ++i) {
            toolIcons[i] = new ImageIcon(toolIconPath[i]);
            toggleButton[i] = new JToggleButton(toolIcons[i]);
            toggleButton[i].addActionListener(this);
            toggleButton[i].setFocusable(false);
            buttonGroup.add(toggleButton[i]);
            jToolBar.add(toggleButton[i]);
            toggleButton[i].setToolTipText(toolButtonName[i]);
        }

        // set a default
        toggleButton[defaultTool].setSelected(true);

        toggleButton[DrawType.LINE].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType(DrawType.LINE);
            }
        });

        toggleButton[DrawType.CIRCLE].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType((DrawType.CIRCLE));
            }
        });

        toggleButton[DrawType.RECTANGLE].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType(DrawType.RECTANGLE);
            }
        });

        toggleButton[DrawType.TEXT].addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                drawPanel.setType(DrawType.TEXT);
                String text = JOptionPane.showInputDialog("Please input the text below:");
                drawPanel.setText(text);
            }
        });

        return jToolBar;
    }

    private JPanel initialiseDrawPanel(Integer width, Integer height) throws IOException {
        drawPanel = new PaintCanvas(socket, self);
        JPanel bigDrawPanel = new JPanel();
        bigDrawPanel.add(drawPanel);
        drawPanel.setBounds(new Rectangle(2, 2, width, height));
        return bigDrawPanel;
    }

    private JPanel initialiseUserList(Integer width, Integer height) {
        // userList Part
        JList userList = new JList(userListModel);
        JPanel usersBlock = new JPanel();
        JPanel managerOpr = new JPanel();

        usersBlock.setLayout(new GridLayout(1, 0, 15, 15));
        usersBlock.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        usersBlock.add(userList);

        JButton removeBtn = new JButton("Remove");
        removeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Remove user clicked:" + userList.getSelectedIndex());
                User targetUser = users.get(userList.getSelectedIndex());

                if (targetUser.getName().equals("MANAGER")) {
                    JOptionPane.showMessageDialog(null,
                            "You cannot kick out yourself.");
                }
                else {
                    try {
                        out.write("REMOVE_USER:" + targetUser.getName());
                        out.newLine();
                        out.flush();
                    } catch (IOException ioException) {
                        JOptionPane.showMessageDialog(null,
                                "Removing " + targetUser.getName() + " has failed.");
                    }

                }

            }
        });

        JLabel title = new JLabel("User List");
        usersBlock.add(new JScrollPane(userList), BorderLayout.CENTER);
        managerOpr.add(removeBtn, BorderLayout.NORTH);

        JPanel userListPanel = new JPanel();
        userListPanel.setLayout(null);
        title.setBounds(0, 0, width, 50);
        usersBlock.setBounds(0, 50, width, height);
        managerOpr.setBounds(60, 60 + height, 100, 60);
        userListPanel.add(title);
        userListPanel.add(usersBlock);

        // only manager can perform removing users
        if (isManager) {
            userListPanel.add(managerOpr);
        }

        return userListPanel;
    }

    private void save() {
        try {
            FileOutputStream out = new FileOutputStream(filePath + fileName + ".whiteboard");
            out.write(drawPanel.getDataList().toString().getBytes());
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveAs() {
        try {
            FileDialog saveDialog = new FileDialog(this,
                    "Please provide a file path", FileDialog.SAVE);
            saveDialog.setVisible(true);

            if (saveDialog.getFile() != null) {
                filePath = saveDialog.getDirectory();
                fileName = saveDialog.getFile();
                FileOutputStream out = new FileOutputStream(filePath + fileName + ".whiteboard");
                out.write(drawPanel.getDataList().toString().getBytes());
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void open() {
        JFileChooser fileDialog = new JFileChooser(filePath.equals("") ? "." : filePath);
        fileDialog.setMultiSelectionEnabled(false);
        fileDialog.addChoosableFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                if (name.endsWith(".whiteboard")) return true;
                return false;
            }

            @Override
            public String getDescription() {
                return null;
            }
        });

        fileDialog.setAcceptAllFileFilterUsed(false);
        fileDialog.showOpenDialog(this);
        File file = fileDialog.getSelectedFile();

        try {
            InputStream in = new FileInputStream(file);
            BufferedReader buf = new BufferedReader(new InputStreamReader(in));
            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();

            while(line != null){
                sb.append(line);
                line = buf.readLine();
            }

            String fileAsString = sb.toString();

            DataSourceList initList = new DataSourceList(fileAsString);
            drawPanel.reset(initList);

        } catch (IOException e) {
            System.out.println("Please provide a valid file");
            JOptionPane.showMessageDialog(this, "Please provide a valid file");
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Please provide a valid file");
            JOptionPane.showMessageDialog(this, "Please provide a valid file");
        }
    }

    public void close() {
        int response = JOptionPane.showConfirmDialog(null, "Do you want to save the whiteboard before closing?"
                , "Confirm", JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            if ("".equals(filePath + fileName)) {
                saveAs();
            }
            else {
                save();
            }
        }

        System.exit(0);
    }
}
