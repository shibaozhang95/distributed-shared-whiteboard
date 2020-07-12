package GUI;

import java.awt.*;

public class GraphicAction {
    Point startPoint;
    Point endPoint;

    public GraphicAction(Point startPoint, Point endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
    }

    public static void drawGraphics(int type, Point startPoint, Point endPoint
            , Color pencilColor, String text, Graphics2D g) {
        switch (type) {
            case DrawType.LINE:
                drawLine(startPoint, endPoint, pencilColor, g);
                break;
            case DrawType.CIRCLE:
                drawCircle(startPoint, endPoint, pencilColor, g);
                break;
            case DrawType.RECTANGLE:
                drawRectangle(startPoint, endPoint, pencilColor, g);
                break;
            case DrawType.TEXT:
                drawText(startPoint, text, pencilColor, g);
                break;
        }
    }

    private static void drawLine(Point startPoint, Point endPoint
            , Color pencilColor, Graphics2D g) {
        g.setColor(pencilColor);
        g.drawLine(startPoint.x, startPoint.y, endPoint.x, endPoint.y);
    }

    private static void drawRectangle(Point startPoint, Point endPoint
            , Color pencilColor, Graphics2D g) {
        g.setColor(pencilColor);

        int width = Math.abs(endPoint.x - startPoint.x);
        int height = Math.abs(endPoint.y - startPoint.y);
        int x = Math.min(startPoint.x, endPoint.x);
        int y = Math.min(startPoint.y, endPoint.y);

        g.drawRect(x, y, width, height);
    }

    private static void drawCircle(Point startPoint, Point endPoint
            , Color pencilColor, Graphics2D g) {
        g.setColor(pencilColor);

        int width = Math.abs(endPoint.x - startPoint.x);
        int height = Math.abs(endPoint.y - startPoint.y);

        if (width < height) {
            int x = Math.min(startPoint.x, endPoint.x);
            int y = Math.min(startPoint.y, endPoint.y) + (height - width) / 2;
            g.drawOval(x, y, width, width);
        }
        else {
            int x = Math.min(startPoint.x, endPoint.x) + (width - height) / 2;
            int y = Math.min(startPoint.y, endPoint.y);
            g.drawOval(x, y, height, height);
        }
    }

    private static void drawText(Point startPoint, String text
            , Color pencilColor, Graphics2D g) {
        g.setColor(pencilColor);
        g.drawString(text, startPoint.x, startPoint.y);
    }
}
