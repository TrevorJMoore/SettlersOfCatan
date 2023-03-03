package src;

import java.awt.*;

public class TileBase extends Polygon
 {
    public static final int SIDES = 6;
    protected Point[] points = new Point[SIDES];
    private Point center = new Point(0, 0);
    private int radius;
    private int rotation = 90;
    private int colorValue;
    private int number;
    private SOC.resource resource;

    public TileBase(Point center, SOC.resource r, int number) {
        npoints = SIDES;
        xpoints = new int[SIDES];
        ypoints = new int[SIDES];

        this.center = center;
        this.radius = 50;
        this.number = number;
        this.resource = r;

        switch (r)
        {
            case WOOD:
            this.colorValue = 0x008844;
            break;
            case SHEEP:
            this.colorValue = 0x33FF33;
            break;
            case ORE:
            this.colorValue = 0xCCCCCC;
            break;
            case WHEAT:
            this.colorValue = 0xFFCC33;
            break;
            case BRICK:
            this.colorValue = 0xCC3300;
            break;
            case DESERT:
            this.colorValue = 0xFFFF00;
            break;

            case EMPTY:
            this.colorValue = 0x4488FF;
        }
        updatePoints();
    }

    public TileBase(int x, int y, SOC.resource r, int number) {
        this(new Point(x, y), r, number);
    }

    private double findAngle(double fraction) {
        return fraction * Math.PI * 2 + Math.toRadians((rotation + 180) % 360);
    }

    private Point findPoint(double angle) {
        int x = (int) (center.x + Math.cos(angle) * radius);
        int y = (int) (center.y + Math.sin(angle) * radius);

        return new Point(x, y);
    }

    protected void updatePoints() {
        for (int p = 0; p < SIDES; p++) {
            double angle = findAngle((double) p / SIDES);
            Point point = findPoint(angle);
            xpoints[p] = point.x;
            ypoints[p] = point.y;
            points[p] = point;
        }
    }

    public void draw(Graphics2D g,int lineThickness, boolean filled) {
        // Store before changing.
        Stroke tmpS = g.getStroke();
        Color tmpC = g.getColor();

        g.setColor(new Color(colorValue));
        g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER));

        if (filled)
            g.fillPolygon(xpoints, ypoints, npoints);
        else
            g.drawPolygon(xpoints, ypoints, npoints);


        if (resource != SOC.resource.EMPTY)
        {
            FontMetrics metrics = g.getFontMetrics();
            g.setColor(new Color(0xFFFFFF));
            String text = String.format("%s",number);
            int w = metrics.stringWidth(text);
            int h = metrics.getHeight();
            g.drawString(text, center.x - w/2, center.y - h/2);

            text = String.format("%s",resource);
            w = metrics.stringWidth(text);
            g.drawString(text, center.x - w/2, center.y + h/2 );
        }

        // Set values to previous when done.
        g.setColor(tmpC);
        g.setStroke(tmpS);
    }

}