package kanga.kcae.view.swing;

import java.awt.geom.Point2D;

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
     *  @param  area        The region to view.
     *  @param  fitMethod   If the region does not match the aspect ratio of
     *                      the screen, this describes how it should be
     *                      adjusted.
     *  @see kanga.kcae.object.Rectangle#adjustAspectRatio(
     *         double, kanga.kcae.object.Rectangle.FitMethod) 
     */
    public void setViewArea(Rectangle area, Rectangle.FitMethod fitMethod);
    
    /** Subscribes a listener to be notified whenever the view area changes.
     * 
     *  @param vacl     A listener to be notified. 
     */
    public void addViewAreaChangeListener(ViewAreaChangeListener vacl);
    
    /** Unsubscribes a listener from being notified about view area changes.
     * 
     *  @param vacl     The listener to no longer receive view area changes.
     */
    public void removeViewAreaChangeListener(ViewAreaChangeListener vacl);
    
    /** Converts a point on the screen to the corresponding quanta point.
     * 
     *  @param  p       The point in screen units.
     *  @return The corresponding database point.
     */
    public Point screenPointToQuanta(Point2D p);
    
    /** Converts a point in the database to a point on the screen.
     * 
     *  This may return a point outside of the current view area.
     *  
     *  @param  p       The point in database units.
     *  @return The corresponding point on the screen.
     */
    public Point2D quantaPointToScreen(Point p);
}
