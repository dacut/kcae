package kanga.kcae.view.swing;

import kanga.kcae.object.Rectangle;
import kanga.kcae.object.Point;

public interface MeasuredView {
    /** Returns the currently visible view area.
     * 
     *  If the view area is undefined, this will return a rectangle of zero
     *  width and height.
     *  
     *  @return The currently visible view area.
     */
    public Rectangle getViewArea();

    /** Sets the current viewport to show the specified region.
     * 
     *  The region is minimally adjusted to match the aspect ratio of the
     *  component.
     * 
     *  @param r            The region to view.
     *  @see kanga.kcae.object.Rectangle#adjustAspectRatio(double, kanga.kcae.object.Rectangle.FitMethod) 
     */
    public void setViewArea(Rectangle area);
    public void addViewAreaChangeListener(ViewAreaChangeListener vacl);
    public void removeViewAreaChangeListener(ViewAreaChangeListener vacl);
    public Point screenPointToQuanta(java.awt.Point p);
    public java.awt.Point quantaPointToScreen(Point p);
}
