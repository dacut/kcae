package kanga.kcae.view.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import static java.lang.Math.pow;
import static java.lang.Math.round;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.BaseUnit;
import kanga.kcae.object.Point;
import kanga.kcae.object.Rectangle;

/** A panel implementing the basic tools needed to interact with a MeasuredView.
 * 
 *  
 */
public class MeasuredViewPanel
    extends JPanel
    implements MeasuredView
{
    static final Log log = LogFactory.getLog(MeasuredViewPanel.class);
    private static final long serialVersionUID = 1L;

    /** Dispatch events to the current tool. */
    class InputEventDispatcher
        implements FocusListener, KeyListener, MouseListener,
                   MouseMotionListener, MouseWheelListener
    {
        @Override
        public void focusGained(final FocusEvent e) {
            final MeasuredViewTool tool =
                MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.focusGained(e);
            }
        }

        @Override
        public void focusLost(final FocusEvent e) {
            final MeasuredViewTool tool =
                MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.focusLost(e);
            }
        }

        @Override
        public void keyTyped(final KeyEvent e) {
            final MeasuredViewTool tool =
                MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.keyTyped(e);
            }
        }

        @Override
        public void keyPressed(final KeyEvent e) {
            final MeasuredViewTool tool =
                MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.keyPressed(e);
            }
        }

        @Override
        public void keyReleased(final KeyEvent e) {
            final MeasuredViewTool tool =
                MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.keyReleased(e);
            }
        }

        @Override
        public void mouseClicked(final MouseEvent e) {
            final MeasuredViewTool tool =
                    MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.mouseClicked(e);
            }
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            log.debug("mousePressed: " + e);
            final MeasuredViewTool tool =
                    MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.mousePressed(e);
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            final MeasuredViewTool tool =
                MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.mouseReleased(e);
            }
        }

        @Override
        public void mouseEntered(final MouseEvent e) {
            final MeasuredViewTool tool =
                MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.mouseEntered(e);
            }
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            final MeasuredViewTool tool =
                MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.mouseExited(e);
            }
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            final MeasuredViewTool tool =
                MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.mouseDragged(e);
            }
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            final MeasuredViewTool tool =
                MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.mouseMoved(e);
            }
        }

        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            final MeasuredViewTool tool =
                MeasuredViewPanel.this.getCurrentTool();
            if (tool != null) {
                tool.mouseWheelMoved(e);
            }
        }
        
    }
    
    protected MeasuredViewPanel() {
        this(null, true, null, BaseUnit.meter);
    }

    protected MeasuredViewPanel(final BaseUnit baseUnit) {
        this(null, true, null, baseUnit);
    }

    protected MeasuredViewPanel(
        final LayoutManager layoutManager,
        final boolean isDoubleBuffered,
        final BaseUnit baseUnit)
    {
        this(layoutManager, isDoubleBuffered, null, baseUnit);
    }
    
    protected MeasuredViewPanel(
        final LayoutManager layoutManager,
        final boolean isDoubleBuffered,
        final Rectangle viewArea,
        final BaseUnit baseUnit)
    {
        super(layoutManager, isDoubleBuffered);
        this.viewAreaChangeListeners = new ArrayList<ViewAreaChangeListener>();
        this.panZoomTool = new PanZoomTool(this);
        this.closedGrabCursor = Resource.getCursor(
            this, Resource.closedGrabImage, 6, 6,
            "MeasuredViewPanelClosedGrab");
        this.openGrabCursor = Resource.getCursor(
            this, Resource.openGrabImage, 6, 6, "MeasuredViewPanelOpenGrab");
        
        final InputEventDispatcher ied = new InputEventDispatcher();
        
        this.addFocusListener(ied);
        this.addKeyListener(ied);
        this.addMouseListener(ied);
        this.addMouseMotionListener(ied);
        this.addMouseWheelListener(ied);
        
        this.setViewArea(viewArea, Rectangle.FitMethod.NEAREST);
        this.setBaseUnit(baseUnit);
        this.setCurrentTool(this.panZoomTool);
        this.setCursor(this.openGrabCursor);

        // Recalculate the view area when the component is resized.
        this.addComponentListener(new RecalculateAspectRatio(this));
        return;
    }
    
    public Pair<Long, Long> getQuantaPerPixel() {
        final Rectangle viewArea = this.getViewArea();
        if (viewArea == null) {
            return null;
        }
        
        final long quantaWidth = viewArea.getWidth();
        final long quantaHeight = viewArea.getHeight();
        final long pixelWidth = this.getWidth();
        final long pixelHeight = this.getHeight();
        final long xQPP = round(((double) quantaWidth) /
                                ((double) pixelWidth));
        final long yQPP = round(((double) quantaHeight) /
                                ((double) pixelHeight));
        
        return new ImmutablePair<Long, Long>(xQPP, yQPP);
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
        final Pair<Long, Long> qpp = this.getQuantaPerPixel();
        final double ppqX = 1.0 / qpp.getLeft().doubleValue();
        final double ppqY = 1.0 / qpp.getRight().doubleValue();
        final java.awt.Rectangle bounds = this.getBounds();
        java.awt.Rectangle clip = g.getClipBounds();

        if (clip == null) {
            clip = bounds;
        }

        g.setColor(this.backgroundColor);
        g.fillRect(clip.x, clip.y, clip.width, clip.height);

        g.translate(0, bounds.getHeight());
        g.scale(ppqX, -ppqY);
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
    
    public BaseUnit getBaseUnit() {
        return this.baseUnit;
    }
    
    public void setBaseUnit(BaseUnit baseUnit) {
        if (baseUnit == null) {
            throw new NullPointerException("baseUnit cannot be null");
        }
        
        this.baseUnit = baseUnit;
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
    
    private Rectangle viewArea;
    private BaseUnit baseUnit;
    protected Color backgroundColor;
    private final List<ViewAreaChangeListener> viewAreaChangeListeners;
    private transient MeasuredViewTool currentTool;
    private final PanZoomTool panZoomTool;
    final Cursor closedGrabCursor;
    final Cursor openGrabCursor;
}
