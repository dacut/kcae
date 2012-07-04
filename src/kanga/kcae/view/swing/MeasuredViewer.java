package kanga.kcae.view.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import javax.swing.JPanel;
import static java.lang.Math.pow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.BaseUnit;
import kanga.kcae.object.Extents;
import kanga.kcae.object.Point;
import kanga.kcae.object.Rectangle;
import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.SwingConstants.VERTICAL;

public abstract class MeasuredViewer<T extends Component & MeasuredView>
    extends JPanel
{
    static final Log log = LogFactory.getLog(MeasuredViewer.class);
    
    class PanningListener
        extends MouseAdapter
        implements MouseMotionListener, MouseWheelListener
    {
        private boolean enabled = true;
        private int lastX = Integer.MIN_VALUE;
        private int lastY = Integer.MIN_VALUE;
        
        @Override
        public void mouseDragged(final MouseEvent e) {
            final int x, y;
            
            log.debug("mouseDragged: " + e);
            
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
                final Ruler hRuler = MeasuredViewer.this.getHorizontalRuler();
                final Ruler vRuler = MeasuredViewer.this.getVerticalRuler();
                final long dxQua = -hRuler.getQuantaPerPixel() * dxPix;
                final long dyQua = -vRuler.getQuantaPerPixel() * dyPix;
             
                final Rectangle originalView = MeasuredViewer.this.getViewArea();
                final Rectangle newView = originalView.translate(dxQua, dyQua);

                log.debug("pan: oldView=" + originalView + "; newView=" + newView);
                MeasuredViewer.this.setViewArea(newView);
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
                log.debug("mousePressed:" + e);
                
                this.lastX = e.getXOnScreen();
                this.lastY = e.getYOnScreen();
            }
        }
        
        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            final int clicks = e.getWheelRotation();
            final MeasuredView viewer = MeasuredViewer.this.getViewer();
            final Point zoomPoint = viewer.screenPointToQuanta(e.getPoint());
            final Rectangle originalView = MeasuredViewer.this.getViewArea();
            final Rectangle newView = originalView.zoom(
                pow(1.05, clicks), zoomPoint);
            
            log.debug("zoom: oldView=" + originalView + "; newView=" + newView + "; clicks=" + clicks);
            MeasuredViewer.this.setViewArea(newView);
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
    
    protected MeasuredViewer(final T viewer, final BaseUnit baseUnit) {
        this(viewer, null, baseUnit);
    }

    protected MeasuredViewer(
        final T viewer,
        final Rectangle viewArea,
        final BaseUnit baseUnit)
    {
        super(new GridBagLayout(), true);
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints cpcons = new GridBagConstraints();
        final GridBagConstraints hrcons = new GridBagConstraints();
        final GridBagConstraints vrcons = new GridBagConstraints();

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

        this.hRuler = new Ruler(HORIZONTAL, baseUnit);
        this.vRuler = new Ruler(VERTICAL, baseUnit);
        this.viewer = viewer;
        
        this.setViewArea(viewArea);
        this.setLayout(layout);
        this.add(this.hRuler, hrcons);
        this.add(this.vRuler, vrcons);
        this.add(this.viewer, cpcons);
        
        this.panningListener = new PanningListener();
        this.viewer.addMouseListener(this.panningListener);
        this.viewer.addMouseMotionListener(this.panningListener);
        this.viewer.addMouseWheelListener(this.panningListener);
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
        if (r != null) {
            // Make sure the aspect ratio is correct.
            java.awt.Rectangle bounds = this.viewer.getBounds();
        
            if (bounds != null && bounds.width > 0 && bounds.height > 0) {
                final double screenAspect = (((double) bounds.width) /
                                             ((double) bounds.height));
                r = r.fitToAspect(screenAspect);
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
    
    private final Ruler hRuler;
    private final Ruler vRuler;
    private final T viewer;
    private final PanningListener panningListener;
    private static final long serialVersionUID = -7664541928680432846L;	
}
