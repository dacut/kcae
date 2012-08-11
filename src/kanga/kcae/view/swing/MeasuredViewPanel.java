package kanga.kcae.view.swing;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
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

public class MeasuredViewPanel
    extends JPanel
    implements MeasuredView
{
    static final Log log = LogFactory.getLog(MeasuredViewPanel.class);
    private static final long serialVersionUID = -4832792841867215854L;

    /** Resize the view area to meet the aspect ratio when the MeasuredViewer
     *  is resized.
     */
    class RecalculateAspectRatio extends ComponentAdapter {
        @Override
        public void componentResized(final ComponentEvent evt) {
            MeasuredViewPanel.this.setViewArea(
                MeasuredViewPanel.this.getViewArea(),
                Rectangle.FitMethod.EXPAND);
        }
    }
    
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
    
    /** A tool for handling pan/zoom requests.
     * 
     *  This is the tool which is typically enabled by default (i.e. when no
     *  other tool is active).  Panning is performed by pressing mouse
     *  button 1, dragging the canvas around, and then releasing mouse
     *  button 1.  Zooming is performed using the mouse scroll wheel.
     */
    class PanZoomHandler extends MeasuredViewToolAdapter {
        private boolean enabled = true;
        private int lastX = Integer.MIN_VALUE;
        private int lastY = Integer.MIN_VALUE;
        
        @Override
        public void mouseDragged(final MouseEvent e) {
            final int x, y;
            
            if (! this.isEnabled()) {
                return;
            }
            
            x = e.getXOnScreen();
            y = e.getYOnScreen();
            
            if (this.lastX != Integer.MIN_VALUE &&
                this.lastY != Integer.MIN_VALUE)
            {
                // We have a previous reference point; use it to calculate the
                // distance in pixels.
                int dxPix = x - this.lastX;
                int dyPix = y - this.lastY;
                
                // Convert from pixels to quanta.
                final Pair<Long, Long> qpp =
                    MeasuredViewPanel.this.getQuantaPerPixel();
                final long dxQua = -qpp.getLeft() * dxPix;
                final long dyQua = -qpp.getRight() * dyPix;
             
                final Rectangle originalView =
                    MeasuredViewPanel.this.getViewArea();
                final Rectangle newView = originalView.translate(dxQua, dyQua);

                log.debug("pan: oldView=" + originalView + "; newView=" +
                          newView);
                MeasuredViewPanel.this.setViewArea(newView);
            }
            
            this.lastX = x;
            this.lastY = y;
            return;
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            return;
        }
        
        @Override
        public void mousePressed(final MouseEvent e) {
            if ((e.getButton() & MouseEvent.BUTTON1) != 0) {
                this.lastX = e.getXOnScreen();
                this.lastY = e.getYOnScreen();
                e.getComponent().setCursor(
                    MeasuredViewPanel.this.closedGrabCursor);
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent e) {
            if ((e.getButton() & MouseEvent.BUTTON1) != 0) {
                e.getComponent().setCursor(
                    MeasuredViewPanel.this.openGrabCursor);
            }
        }
        
        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            final int clicks = e.getWheelRotation();
            final Point zoomPoint = MeasuredViewPanel.this.screenPointToQuanta(
                e.getPoint());
            final Rectangle originalView =
                MeasuredViewPanel.this.getViewArea();
            final Rectangle newView = originalView.zoom(
                pow(1.05, clicks), zoomPoint);
            
            log.debug("zoom: oldView=" + originalView + "; newView=" +
                      newView + "; clicks=" + clicks);
            MeasuredViewPanel.this.setViewArea(newView);
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
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
        this.panningListener = new PanZoomHandler();
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
        
        this.setViewArea(viewArea);
        this.setBaseUnit(baseUnit);
        this.setCurrentTool(this.panningListener);
        this.setCursor(this.openGrabCursor);

        // Recalculate the view area when the component is resized.
        this.addComponentListener(new RecalculateAspectRatio());
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
    
    @Override
    public void setViewArea(final Rectangle viewArea) {
        this.setViewArea(viewArea, Rectangle.FitMethod.NEAREST);
    }

    /** Sets the current viewport to show the specified region.
     * 
     *  @param viewarea     The region to view.
     *  @param fitMethod    If the region does not match the aspect ratio of
     *      of the screen, this specifies how the bounds should be adjusted.
     *  @see kanga.kcae.object.Rectangle#adjustAspectRatio(double, kanga.kcae.object.Rectangle.FitMethod) 
     */
    public void setViewArea(
        Rectangle viewArea,
        final Rectangle.FitMethod fitMethod)
    {
        if (viewArea != null) {
            // Make sure the aspect ratio is correct.
            java.awt.Rectangle bounds = this.getBounds();
        
            if (bounds != null && bounds.width > 0 && bounds.height > 0) {
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
    public Point screenPointToQuanta(java.awt.Point p) {
        final Rectangle va = this.getViewArea();
        final java.awt.Rectangle bounds = this.getBounds();
        final double xrel = (p.getX() - bounds.getX()) / bounds.getWidth();
        final double yrel = (p.getY() - bounds.getY()) / bounds.getHeight();
        
        return new Point(va.getLeft() + (long) (va.getWidth() * xrel),
                         va.getTop() + (long) (va.getHeight() * yrel));
    }
    
    @Override
    public java.awt.Point quantaPointToScreen(Point p) {
        final Rectangle va = this.getViewArea();
        final java.awt.Rectangle bounds = this.getBounds();
        final double xrel = ((double) (p.getX() - va.getLeft())) /
                            (double) va.getWidth();
        final double yrel = ((double) (p.getY() - va.getTop())) /
                            (double) va.getHeight();
        
        return new java.awt.Point(bounds.x + (int) (bounds.getWidth() * xrel),
                                  bounds.y + (int) (bounds.getHeight() * yrel));
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

        g.scale(ppqX, ppqY);
        g.translate(-viewArea.getLeft(), -viewArea.getTop());
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
    
    private Rectangle viewArea;
    private BaseUnit baseUnit;
    protected Color backgroundColor;
    private final List<ViewAreaChangeListener> viewAreaChangeListeners;
    private MeasuredViewTool currentTool;
    private final PanZoomHandler panningListener;
    final Cursor closedGrabCursor;
    final Cursor openGrabCursor;
}
