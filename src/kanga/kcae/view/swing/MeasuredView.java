package kanga.kcae.view.swing;

import kanga.kcae.object.Rectangle;
import kanga.kcae.object.Point;

public interface MeasuredView {
    public Rectangle getViewArea();
    public void setViewArea(Rectangle area);
    public Point screenPointToQuanta(java.awt.Point p);
    public java.awt.Point quantaPointToScreen(Point p);
}
