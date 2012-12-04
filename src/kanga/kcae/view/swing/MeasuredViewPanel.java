package kanga.kcae.view.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.swing.JPanel;
import static java.lang.Math.pow;
import static java.lang.Math.round;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.Dimension;
import kanga.kcae.object.Point;
import kanga.kcae.object.Rectangle;

/** A panel which handles the core events needed to interact with a
 *  {@link MeasuredView}.
 * 
 *  <p>Unfortunately, it's impossible to make a usable modeless interface here
 *  with standard human interface devices (mouse and keyboard).  Thus, this
 *  class introduces the concept of {@linkplain MeasuredViewTool tools} which
 *  can handle the HID events coming in to manipulate the underlying view.
 *  At any given instant, one tool is designated the current tool; this is the
 *  tool to which all HID events are dispatched.</p>
 *  
 *  <p>Indicating to the user which tool is currently active is typically done
 *  via cursor shapes (implemented by the tool) and button indicators
 *  (implemented by the surrounding frame components).</p>
 */
public class MeasuredViewPanel
    extends JPanel
    implements MeasuredView
{
    private static final Log log = LogFactory.getLog(MeasuredViewPanel.class);
    private static final long serialVersionUID = 1L;

    /** Create a new MeasuredViewPanel using a
     *  {@link java.awt.FlowLayout FlowLayout} manager, with double-buffering
     *  enabled, and with a {@code null} view area.
     * 
     *  <p>Since the initial view area is null, the display will be empty
     *  until the view area is set.</p>
     */
    protected MeasuredViewPanel() {
        this(null, true, null);
    }

    /** Create a new MeasuredViewPanel with a {@code null} view area.
     * 
     *  <p>Since the initial view area is null, the display will be empty
     *  until the view area is set.</p>
     * 
     *  @param  layoutManager The layout manager to use.  If {@code null}, a
     *          {@link java.awt.FlowLayout} is used.
     *  @param  isDoubleBuffered If {@code true}, additional memory will be used
     *          for fast, flicker-free updates.
     */
    protected MeasuredViewPanel(
        final LayoutManager layoutManager,
        final boolean isDoubleBuffered)
    {
        this(layoutManager, isDoubleBuffered, null);
    }
    
    /** Create a new MeasuredViewPanel.
     * 
     *  @param  layoutManager The layout manager to use.  If {@code null}, a
     *          {@link java.awt.FlowLayout} is used.
     *  @param  isDoubleBuffered If {@code true}, additional memory will be used
     *          for fast, flicker-free updates.
     *  @param  viewArea The initial view area to show.  If {@code null},
     *          nothing will be drawn. 
     */
    protected MeasuredViewPanel(
        @CheckForNull final LayoutManager layoutManager,
        final boolean isDoubleBuffered,
        @CheckForNull final Rectangle viewArea)
    {
        super(layoutManager, isDoubleBuffered);
        this.viewAreaChangeListeners = new ArrayList<ViewAreaChangeListener>();
        this.panZoomTool = new PanZoomTool(this);
        this.closedGrabCursor = Resource.getCursor(
            this, Resource.closedGrabImage, 6, 6,
            "MeasuredViewPanelClosedGrab");
        this.openGrabCursor = Resource.getCursor(
            this, Resource.openGrabImage, 6, 6, "MeasuredViewPanelOpenGrab");
        
        final MeasuredViewPanelEventDispatcher ied = new MeasuredViewPanelEventDispatcher(this);
        
        this.addFocusListener(ied);
        this.addKeyListener(ied);
        this.addMouseListener(ied);
        this.addMouseMotionListener(ied);
        this.addMouseWheelListener(ied);
        
        this.setViewArea(viewArea, Rectangle.FitMethod.NEAREST);
        this.setCurrentTool(this.panZoomTool);
        this.setCursor(this.openGrabCursor);

        // Recalculate the view area when the component is resized.
        this.addComponentListener(new RecalculateAspectRatio(this));
        return;
    }
    
    /** Returns the number of nanometers represented by the distance between
     *  pixel centers (in the x and y directions).
     *  
     *  <p>If the view area is {@code null}, the return value here is also
     *  {@code null}.</p>
     * 
     *  @return The number of nanometers represented by the distance between
     *          pixel centers.
     */
    @CheckForNull
    public Dimension getNanometersPerPixel() {
        final Rectangle viewArea = this.getViewArea();
        if (viewArea == null) {
            return null;
        }
        
        final double nmWidth = viewArea.getWidth();
        final double nmHeight = viewArea.getHeight();
        final double pixelWidth = this.getWidth();
        final double pixelHeight = this.getHeight();
        final long xNmPP = round(nmWidth / pixelWidth);
        final long yNmPP = round(nmHeight / pixelHeight);
        
        return new Dimension(xNmPP, yNmPP);
    }
    
    @Override
    public Rectangle getViewArea() {
        return this.viewArea;
    }
    
    /** Sets the current viewport to show the specified region.
     * 
     *  @param viewArea     The region to view.
     *  @param fitMethod    If the region does not match the aspect ratio of
     *      of the screen, this specifies how the bounds should be adjusted.
     *  @see kanga.kcae.object.Rectangle#adjustAspectRatio(
     *         double, kanga.kcae.object.Rectangle.FitMethod) 
     */
    @Override
    public void setViewArea(
        Rectangle viewArea,
        final Rectangle.FitMethod fitMethod)
    {
        if (viewArea != null) {
            // Make sure the aspect ratio is correct.
            java.awt.Rectangle bounds = this.getBounds();
            assert bounds != null;
        
            if (bounds.width > 0 && bounds.height > 0) {
                final double screenAspect = (((double) bounds.width) /
                                             ((double) bounds.height));
                final Rectangle fitted = viewArea.adjustAspectRatio(
                   screenAspect, fitMethod);
                log.debug("setViewArea: resizing from " + viewArea + " to " +
                          fitted);
                viewArea = fitted;
            } else {
                log.debug("Cannot perform aspect ratio correction; bounds " +
                          "have not been established");
            }
        }

        final Rectangle oldViewArea = this.viewArea;
        this.viewArea = viewArea;
        final ViewAreaChangeEvent evt = new ViewAreaChangeEvent(
            this, oldViewArea, viewArea);

        for (final ViewAreaChangeListener vacl : this.viewAreaChangeListeners) {
            vacl.viewAreaChanged(evt);
        }

        this.repaint();
    }

    @Override
    public void addViewAreaChangeListener(final ViewAreaChangeListener vacl) {
        this.viewAreaChangeListeners.add(vacl);
    }
    
    @Override
    public void removeViewAreaChangeListener(
        final ViewAreaChangeListener vacl)
    {
        this.viewAreaChangeListeners.remove(vacl);
    }
    
    @Override
    public Point screenPointToQuanta(Point2D p) {
        final Rectangle va = this.getViewArea();
        final java.awt.Rectangle bounds = this.getBounds();
        
        final double xrel = p.getX() / bounds.getWidth();
        final double yrel = p.getY() / bounds.getHeight();
        
        return new Point(va.getLeft() + (long) (va.getWidth() * xrel),
                         va.getTop() - (long) (va.getHeight() * yrel));
    }
    
    @Override
    public Point2D quantaPointToScreen(Point p) {
        final Rectangle va = this.getViewArea();
        final java.awt.Rectangle bounds = this.getBounds();
        final double xrel = ((double) (p.getX() - va.getLeft())) /
                             (double) va.getWidth();
        final double yrel = ((double) (va.getTop() - p.getY())) /
                             (double) va.getHeight();
        
        Point2D result = new Point2D.Double(bounds.getWidth() * xrel,
                                            bounds.getHeight() * yrel);
        return result;
    }
    
    @Override
    protected void paintComponent(final Graphics graphics) {
        final Rectangle viewArea = this.getViewArea();
        if (viewArea == null) {
            log.warn("Cannot paint: viewArea is null");
            return;
        }
        
        final Graphics2D g = (Graphics2D) graphics;
        final Dimension nmpp = this.getNanometersPerPixel();
        
        if (nmpp == null) {
            log.warn("Cannot paint: nanometersPerPixel is null");
            return;
        }
        
        final double ppnmX = 1.0 / nmpp.getWidth();
        final double ppnmY = 1.0 / nmpp.getHeight();
        final java.awt.Rectangle bounds = this.getBounds();
        java.awt.Rectangle clip = g.getClipBounds();

        if (clip == null) {
            clip = bounds;
        }

        g.setColor(this.backgroundColor);
        g.fillRect(clip.x, clip.y, clip.width, clip.height);

        g.translate(0, bounds.getHeight());
        g.scale(ppnmX, -ppnmY);
        g.translate(-viewArea.getLeft(), -viewArea.getBottom());
    }
    
    /** Zooms in/out while keeping the specified point on the screen fixed.
     * 
     *  @param  screenPoint     The point to keep fixed.
     *  @param  magnitude       The magnitude to zoom in/out.  Positive numbers
     *                          zoom in.
     */
    public void zoomAtScreenPoint(
        final Point2D screenPoint,
        final int magnitude)
    {
        log.debug("zoomAtScreenPoint: sp=" + screenPoint + ", mag=" + magnitude);
        this.zoomAtQuantaPoint(this.screenPointToQuanta(screenPoint),
                               magnitude);
    }
    
    /** Zooms in/out while keeping the specified point fixed.
     * 
     *  @param  zoomPoint       The point to keep fixed.
     *  @param  magnitude       The magnitude to zoom in/out.  Positive numbers
     *                          zoom in.
     */
    public void zoomAtQuantaPoint(
        final Point zoomPoint,
        final int magnitude)
    {
        final Rectangle originalView =
            MeasuredViewPanel.this.getViewArea();
        final Rectangle newView = originalView.zoom(
            pow(1.05, magnitude), zoomPoint);
        
        log.debug("zoomAtQuantaPoint: zp=" + zoomPoint + ", mag=" + magnitude);
        
        // Don't allow the user to zoom in greater than 1 nm per pixel --
        // strangeness abounds when multiple screen points map to the same
        // quanta.
        if (magnitude < 0 || newView.getWidth() > this.getBounds().width) { 
            this.setViewArea(newView, Rectangle.FitMethod.NEAREST);
        }
    }
    
    /** Sets the tool to receive input events.
     * 
     *  @param tool     The tool to receive input events.
     */
    public void setCurrentTool(final MeasuredViewTool tool) {
        if (this.currentTool != tool) {
            if (this.currentTool != null) {
                this.currentTool.disabled();
            }
            
            this.currentTool = tool;
            tool.enabled();
        }
    }
    
    /** Returns the tool currently receiving input events.
     * 
     *  @return The tool currently receiving input events.
     */
    public MeasuredViewTool getCurrentTool() {
        return this.currentTool;
    }
    
    public PanZoomTool getPanZoomTool() {
        return this.panZoomTool;
    }
    
    @CheckForNull private Rectangle viewArea;
    protected Color backgroundColor;
    private final List<ViewAreaChangeListener> viewAreaChangeListeners;
    private transient MeasuredViewTool currentTool;
    private final PanZoomTool panZoomTool;
    final Cursor closedGrabCursor;
    final Cursor openGrabCursor;
}
