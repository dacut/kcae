package kanga.kcae.view.swing;

import java.awt.geom.Path2D;

import kanga.kcae.object.ArcTo;
import kanga.kcae.object.PathPainter;
import kanga.kcae.object.Point;

public class AWTPathPainter implements PathPainter {
    public AWTPathPainter(final Path2D.Double awtPath) {
        this.awtPath = awtPath;
        this.currentPoint = null;
    }

    @Override
    public void moveTo(Point p) {
        this.awtPath.moveTo((double) p.getX(), (double) p.getY());
        this.currentPoint = p;
    }

    @Override
    public void lineTo(Point p) {
        this.awtPath.lineTo((double) p.getX(), (double) p.getY());
        this.currentPoint = p;
    }
    
    @Override
    public void arcTo(
        Point center,
        double angleRadians)
    {
        this.currentPoint = ArcTo.paintAsBezier(
            this, this.currentPoint, center, angleRadians);
    }

    @Override
    public void quadraticCurveTo(Point control, Point target) {
        this.awtPath.quadTo(
            (double) control.getX(), (double) control.getY(),
            (double) target.getX(), (double) target.getY());
        this.currentPoint = target;
    }

    @Override
    public void bezierCurveTo(
        Point control1,
        Point control2,
        Point target)
    {
        this.awtPath.curveTo(
            (double) control1.getX(), (double) control1.getY(),
            (double) control2.getX(), (double) control2.getY(),
            (double) target.getX(), (double) target.getY());
        this.currentPoint = target;
    }

    @Override
    public void closePath() {
        this.awtPath.closePath();
        this.currentPoint = null;
    }

    private Point currentPoint;
    private final Path2D.Double awtPath;
}