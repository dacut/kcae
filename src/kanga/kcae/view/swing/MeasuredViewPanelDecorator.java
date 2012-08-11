package kanga.kcae.view.swing;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import static java.lang.Math.pow;
import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.SwingConstants.VERTICAL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.BaseUnit;
import kanga.kcae.object.Extents;
import kanga.kcae.object.Point;
import kanga.kcae.object.Rectangle;

/** A component which places rulers around a MeasuredView panel and dispatches
 *  input events to it.
 * 
 *  This component ensures that the view areas of the {@link MeasuredView}
 *  and the surrounding {@link Ruler} objects stay in sync.
 *  
 *  @param <T>  The actual type of the {@code MeasuredView} object.
 */
public abstract class MeasuredViewPanelDecorator<T extends Component & MeasuredView>
    extends JPanel
{
    static final Log log = LogFactory.getLog(MeasuredViewPanelDecorator.class);
    
    /** Dispatch events to the current tool. */
    class InputEventDispatcher implements MeasuredViewerTool {
        @Override
        public void focusGained(final FocusEvent e) {
            final MeasuredViewerTool tool =
                MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.focusGained(e);
            }
        }

        @Override
        public void focusLost(final FocusEvent e) {
            final MeasuredViewerTool tool =
                MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.focusLost(e);
            }
        }

        @Override
        public void keyTyped(final KeyEvent e) {
            final MeasuredViewerTool tool =
                MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.keyTyped(e);
            }
        }

        @Override
        public void keyPressed(final KeyEvent e) {
            final MeasuredViewerTool tool =
                MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.keyPressed(e);
            }
        }

        @Override
        public void keyReleased(final KeyEvent e) {
            final MeasuredViewerTool tool =
                MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.keyReleased(e);
            }
        }

        @Override
        public void mouseClicked(final MouseEvent e) {
            final MeasuredViewerTool tool =
                    MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.mouseClicked(e);
            }
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            final MeasuredViewerTool tool =
                    MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.mousePressed(e);
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            final MeasuredViewerTool tool =
                MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.mouseReleased(e);
            }
        }

        @Override
        public void mouseEntered(final MouseEvent e) {
            final MeasuredViewerTool tool =
                MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.mouseEntered(e);
            }
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            final MeasuredViewerTool tool =
                MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.mouseExited(e);
            }
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            final MeasuredViewerTool tool =
                MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.mouseDragged(e);
            }
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            final MeasuredViewerTool tool =
                MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.mouseMoved(e);
            }
        }

        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            final MeasuredViewerTool tool =
                MeasuredViewPanelDecorator.this.getCurrentTool();
            if (tool != null) {
                tool.mouseWheelMoved(e);
            }
        }
        
    }
    
    /** A tool for handling pan/zoom requests.
     * 
     *  This is the tool which is typically enabled by default (i.e. when no
     *  other tool is active).  Panning is accomplished by pressing mouse
     *  button 1, dragging the canvas around, and then releasing mouse button
     *  1.  Zooming is accomplished by using the mouse scroll wheel.
     */
    class PanZoomHandler extends MeasuredViewerToolAdapter {
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
                final Ruler hRuler = MeasuredViewPanelDecorator.this.getHorizontalRuler();
                final Ruler vRuler = MeasuredViewPanelDecorator.this.getVerticalRuler();
                final long dxQua = -hRuler.getQuantaPerPixel() * dxPix;
                final long dyQua = -vRuler.getQuantaPerPixel() * dyPix;
             
                final Rectangle originalView =
                    MeasuredViewPanelDecorator.this.getViewArea();
                final Rectangle newView = originalView.translate(dxQua, dyQua);

                log.debug("pan: oldView=" + originalView + "; newView=" +
                          newView);
                MeasuredViewPanelDecorator.this.setViewArea(newView);
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
                    MeasuredViewPanelDecorator.this.closedGrabCursor);
            }
        }
        
        @Override
        public void mouseReleased(final MouseEvent e) {
            if ((e.getButton() & MouseEvent.BUTTON1) != 0) {
                e.getComponent().setCursor(MeasuredViewPanelDecorator.this.openGrabCursor);
            }
        }
        
        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            final int clicks = e.getWheelRotation();
            final MeasuredView viewer = MeasuredViewPanelDecorator.this.getViewer();
            final Point zoomPoint = viewer.screenPointToQuanta(e.getPoint());
            final Rectangle originalView = MeasuredViewPanelDecorator.this.getViewArea();
            final Rectangle newView = originalView.zoom(
                pow(1.05, clicks), zoomPoint);
            
            log.debug("zoom: oldView=" + originalView + "; newView=" +
                      newView + "; clicks=" + clicks);
            MeasuredViewPanelDecorator.this.setViewArea(newView);
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
    
    /** Resize the view area to meet the aspect ratio when the MeasuredViewer
     *  is resized.
     */
    class RecalculateAspectRatio extends ComponentAdapter {
        @Override
        public void componentResized(final ComponentEvent evt) {
            MeasuredViewPanelDecorator.this.setViewArea(
                MeasuredViewPanelDecorator.this.getViewArea(), Rectangle.FitMethod.EXPAND);
        }
    }
    
    protected MeasuredViewPanelDecorator(final T viewer, final BaseUnit baseUnit) {
        this(viewer, null, baseUnit);
    }

    /** Create a new MeasuredViewer container.
     * 
     *  @param view     The interior view.
     *  @param viewArea The initial view area, or {@code null} if the initial
     *      view area is undefined.
     *  @param baseUnit The base units to use for drawing ruler ticks and
     *      labels.
     */
    protected MeasuredViewPanelDecorator(
        final T view,
        final Rectangle viewArea,
        final BaseUnit baseUnit)
    {
        super(new GridBagLayout(), true);
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints cpcons = new GridBagConstraints();
        final GridBagConstraints hrcons = new GridBagConstraints();
        final GridBagConstraints vrcons = new GridBagConstraints();
        final GridBagConstraints imgcons = new GridBagConstraints();

        cpcons.gridx = 1;
        cpcons.gridy = 1;
        cpcons.fill = GridBagConstraints.BOTH;
        cpcons.weightx = 1.0;
        cpcons.weighty = 1.0;
        
        hrcons.gridx = 1;
        hrcons.gridy = 0;
        hrcons.fill = GridBagConstraints.BOTH;
        hrcons.weightx = 1.0;
        
        vrcons.gridx = 0;
        vrcons.gridy = 1;
        vrcons.fill = GridBagConstraints.BOTH;
        vrcons.weighty = 1.0;

        imgcons.gridx = 0;
        imgcons.gridy = 2;
        imgcons.fill = GridBagConstraints.BOTH;

        this.hRuler = new Ruler(HORIZONTAL, baseUnit);
        this.vRuler = new Ruler(VERTICAL, baseUnit);
        this.viewer = view;

        final Image closedGrabImage = Resource.getImage("ClosedGrab.png");
        final Image openGrabImage = Resource.getImage("OpenGrab.png");
        assert closedGrabImage != null;
        assert openGrabImage != null;
        this.closedGrabCursor = Resource.getCursor(
            this, closedGrabImage, 6, 6, "measuredViewerClosedGrab");
        this.openGrabCursor = Resource.getCursor(
            this, openGrabImage, 6, 6, "measuredViewerOpenGrab");
        
        this.setViewArea(viewArea);
        this.setLayout(layout);
        this.add(this.hRuler, hrcons);
        this.add(this.vRuler, vrcons);
        this.add(this.viewer, cpcons);
        
        ImageIcon icon = new ImageIcon(closedGrabImage);
        JButton button = new JButton(icon);
        this.add(button, imgcons);
        
        this.panningListener = new PanZoomHandler();
        this.setCurrentTool(this.panningListener);
        
        this.addComponentListener(new RecalculateAspectRatio());
        this.setCursor(this.openGrabCursor);
    }

    public Ruler getHorizontalRuler() {
        return this.hRuler;
    }

    public Ruler getVerticalRuler() {
        return this.vRuler;
    }
    
    public Rectangle getViewArea() {
        final Extents.Long hExtents = this.hRuler.getQuantaExtents();
        final Extents.Long vExtents = this.vRuler.getQuantaExtents();
        
        return Rectangle.fromExtents(hExtents, vExtents);
    }
    
    public void setViewArea(Rectangle r) {
        this.setViewArea(r, Rectangle.FitMethod.NEAREST);
    }

    /** Sets the current viewport to show the specified region.
     * 
     *  @param r            The region to view.
     *  @param fitMethod    If the region does not match the aspect ratio of
     *      of the screen, this specifies how the bounds should be adjusted.
     *  @see kanga.kcae.object.Rectangle#adjustAspectRatio(double, kanga.kcae.object.Rectangle.FitMethod) 
     */
    public void setViewArea(Rectangle r, final Rectangle.FitMethod fitMethod) {
        if (r != null) {
            // Make sure the aspect ratio is correct.
            java.awt.Rectangle bounds = this.viewer.getBounds();
        
            if (bounds != null && bounds.width > 0 && bounds.height > 0) {
                final double screenAspect = (((double) bounds.width) /
                                             ((double) bounds.height));
                final Rectangle fitted = r.adjustAspectRatio(screenAspect, fitMethod);
                log.debug("setViewArea: resizing from " + r + " to " + fitted);
                r = fitted;
            } else {
                log.debug("Cannot perform aspect ratio correction; bounds " +
                          "have not been established");
            }
            
            this.hRuler.setQuantaExtents(r.getHorizontalExtents());
            this.vRuler.setQuantaExtents(r.getVerticalExtents());
        } else {
            this.hRuler.setQuantaExtents(null);
            this.vRuler.setQuantaExtents(null);
        }
        this.viewer.setViewArea(r);
    }

    public T getViewer() {
        return this.viewer;
    }

    /**Sets the tool to receive input events.
     * 
     *  @param tool     The tool to receive input events.
     */
    public void setCurrentTool(final MeasuredViewerTool tool) {
        this.currentTool = tool;
    }
    
    /** Returns the tool currently receiving input events.
     * 
     *  @return The tool currently receiving input events.
     */
    public MeasuredViewerTool getCurrentTool() {
        return this.currentTool;
    }

    final Cursor closedGrabCursor;
    final Cursor openGrabCursor;
    private final Ruler hRuler;
    private final Ruler vRuler;
    private final T viewer;
    private final PanZoomHandler panningListener;
    private MeasuredViewerTool currentTool;
    private static final long serialVersionUID = -7664541928680432846L;
}
