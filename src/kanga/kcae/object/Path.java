package kanga.kcae.object;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Path implements Shape {
    public Path() {
        this.points = new ArrayList<Point>();
        this.lineStyle = null;
        this.fillStyle = null;
    }

    @Override
    public Rectangle getBoundingBox() {
        final Iterator<Point> i = this.points.iterator();
        final Point firstPoint;
        Rectangle result;

        if (! i.hasNext()) { return null; }
        
        firstPoint = i.next();
        result = new Rectangle(firstPoint, firstPoint);

        while (i.hasNext()) {
            result = result.union

        Rectangle result = null;

        for (final Shape shape : this.getShapes()) {
            final Rectangle shapeBBox = shape.getBoundingBox();

            if (result == null) { result = shapeBBox; }
            else                { result = result.union(shapeBBox); }
        }

        return result;
    }

    @Override
    public void setLineStyle(final LineStyle lineStyle) {
        for (final Shape shape : this.shapes) {
            shape.setLineStyle(lineStyle);
        }
    }

    @Override
    public void setFillStyle(final FillStyle fillStyle) {
        for (final Shape shape : this.shapes) {
            shape.setFillStyle(fillStyle);
        }
    }

    @Override
    public void draw(final Graphics2D graphics) {
        for (final Shape shape : this.getShapes()) {
            shape.draw(graphics);
        }
    }
}