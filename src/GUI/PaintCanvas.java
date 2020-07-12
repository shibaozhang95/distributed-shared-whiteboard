package GUI;

import DataSource.DataSource;
import DataSource.DataSourceList;
import DataSource.User;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.*;
import java.net.Socket;

public class PaintCanvas extends Canvas implements MouseListener, MouseMotionListener {
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private User self;

    private Image screenBuffer;
    private Graphics screenGraphic;

    private int type;

    private DataSourceList dataList;

    private Color pencilColor;
    private String text = "";


    public void setText(String text) {
        this.text = text;
    }


    public PaintCanvas(Socket socket, User self) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        this.self = self;

        type = DrawType.LINE;
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);
        pencilColor = this.getForeground();
        dataList = new DataSourceList();

        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (type == DrawType.TEXT) {
            int startX = e.getX();
            int startY = e.getY();

            DataSource data = new DataSource(self.getName(), type, new Point(startX, startY), new Point(startX, startY)
                    , pencilColor, text);
            dataList.addData(data);

            if (type == DrawType.TEXT) {
                data.setEndPoint(new Point(startX, startY));
                repaint();
            }
            sendDrawingMessage("DRAW:" + data.toString());
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (type != DrawType.TEXT) {
            int startX = e.getX();
            int startY = e.getY();

            DataSource data = new DataSource(self.getName(), type, new Point(startX, startY), new Point(startX, startY)
                    , pencilColor, "");
            dataList.addData(data);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (type != DrawType.TEXT) {
            DataSource cur = dataList.findDataByUsername(self.getName());

            if (cur != null) {
                cur.setEndPoint(e.getPoint());
                sendDrawingMessage("DRAW:" + cur.toString());
            }

            this.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (type != DrawType.TEXT) {
            DataSource cur = dataList.findDataByUsername(self.getName());

            if (cur != null) {
                cur.setEndPoint(e.getPoint());
            }

            this.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }

    public void update(Graphics graphics) {
        screenBuffer = createImage(getWidth(), getHeight());
        screenGraphic = screenBuffer.getGraphics();
        paint(screenGraphic);
        screenGraphic.dispose();
        graphics.drawImage(screenBuffer, 0, 0, null);
        screenBuffer.flush();
    }

    public void paint(Graphics graphics) {
        synchronized (dataList) {
            for (int i = 0, len = dataList.list.size(); i < len; ++i) {
                DataSource data = dataList.list.get(i);

                GraphicAction.drawGraphics(data.getDrawType(), data.getStartPoint()
                        , data.getEndPoint(), data.getPencilColor(), data.getText()
                        , (Graphics2D) graphics);
            }
        }
    }

    public void clear() {
        sendDrawingMessage("CLEAR:");
    }

    public void reset(DataSourceList dataList) {
        sendDrawingMessage("INIT:" + dataList.toString());
    }

    public void sendDrawingMessage(String dataBuffer) {
        try {

            out.write(dataBuffer);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public DataSourceList getDataList() {
        return dataList;
    }

    public void setDataList(DataSourceList dataList) {
        this.dataList = dataList;
    }

    public void setType(int type) {
        this.type = type;
    }
}
