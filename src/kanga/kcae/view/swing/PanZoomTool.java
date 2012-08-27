package kanga.kcae.view.swing;

import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

import kanga.kcae.object.Rectangle;

import org.apache.commons.lang3.tuple.Pair;

/** A tool for handling pan/zoom requests.
 * 
 *  This is the tool which is typically enabled by default (i.e. when no
 *  other tool is active).  Panning is performed by pressing mouse
 *  button 1, dragging the canvas around, and then releasing mouse
 *  button 1.  Zooming is performed using the mouse scroll wheel.
 */
public class PanZoomTool extends MeasuredViewToolAdapter {
    public PanZoomTool(final MeasuredViewPanel panel) {
        this.panel = panel;
    }
    
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
            final Pair<Long, Long> qpp = this.panel.getQuantaPerPixel();
            final long dxQua = -qpp.getLeft() * dxPix;
            final long dyQua = -qpp.getRight() * dyPix;
         
            final Rectangle originalView = this.panel.getViewArea();
            final Rectangle newView = originalView.translate(dxQua, dyQua);
            this.panel.setViewArea(newView, Rectangle.FitMethod.EXPAND);
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
            e.getComponent().setCursor(this.panel.closedGrabCursor);
        }
    }
    
    @Override
    public void mouseReleased(final MouseEvent e) {
        if ((e.getButton() & MouseEvent.BUTTON1) != 0) {
            e.getComponent().setCursor(this.panel.openGrabCursor);
        }
    }
    
    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        this.panel.zoomAtScreenPoint(e.getPoint(), e.getWheelRotation());
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    private final MeasuredViewPanel panel;
    private boolean enabled = true;
    private int lastX = Integer.MIN_VALUE;
    private int lastY = Integer.MIN_VALUE;
}
