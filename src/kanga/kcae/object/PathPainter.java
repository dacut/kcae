package kanga.kcae.object;

public interface PathPainter {
    public void moveTo(Point p);
    public void lineTo(Point p);
    public void quadraticCurveTo(Point control, Point target);
    public void bezierCurveTo(Point control1, Point control2, Point target);
    public void closePath();
}