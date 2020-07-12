package DataSource;

import java.awt.*;

public class DataSource {
    private String username;
    private int drawType;
    private Color pencilColor = Color.BLACK;
    private String text = "";
    private Point startPoint;
    private Point endPoint;

    public DataSource () {
        drawType = 0;
        startPoint = new Point(0, 0);
        endPoint = new Point(0, 0);
    }

    public DataSource(String paramsStr) {
        String params[] = paramsStr.split("\\.");
        this.username = params[0];
        this.drawType = Integer.parseInt(params[1]);
        this.startPoint = new Point(
                Integer.parseInt(params[2].split(",")[0]),
                Integer.parseInt(params[2].split(",")[1])
        );
        this.endPoint = new Point(
                Integer.parseInt(params[3].split(",")[0]),
                Integer.parseInt(params[3].split(",")[1])
        );
        this.text = params.length >= 5 ? params[4] : "";
    }

    public String toString() {
        return username + "." + drawType + "." +
                startPoint.x + "," + startPoint.y + '.' +
                endPoint.x + "," + endPoint.y + '.'
                + text;
    }

    public DataSource (String username, int drawType, Point startPoint, Point endPoint
            , Color pencilColor, String text) {
        this.username = username;
        this.drawType = drawType;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.pencilColor = pencilColor;
        this.text = text;
    }

    public String getUsername() {
        return username;
    }

    public int getDrawType() {
        return drawType;
    }

    public Color getPencilColor() {
        return pencilColor;
    }

    public String getText() {
        return text;
    }

    public Point getStartPoint() { return startPoint; }
    public Point getEndPoint() { return endPoint; }
    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }
}
