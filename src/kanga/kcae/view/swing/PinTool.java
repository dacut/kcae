package kanga.kcae.view.swing;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import kanga.kcae.object.Pin;
import kanga.kcae.object.Point;
import kanga.kcae.object.SignalDirection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PinTool extends MeasuredViewToolAdapter {
    private static final Log log = LogFactory.getLog(PinTool.class);
    
    public PinTool(final SymbolView symView) {
        this.symView = symView;
        this.pin = null;
    }
    
    @Override
    public void enabled() {
        log.debug("enabled");
        this.pin = new Pin(null, (String) null, SignalDirection.PASSIVE,
                           new Point(-30000000, 0), new Point(0, 0));
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        final Point oldEndPoint = this.pin.getEndPoint();
        final Point oldConPoint = this.pin.getConnectionPoint();
        final Point newEndPoint = this.symView.roundToGrid(
            this.symView.screenPointToQuanta(e.getPoint()));
        final long dX = newEndPoint.getX() - oldEndPoint.getX();
        final long dY = newEndPoint.getY() - oldEndPoint.getY();
        final Point newConPoint = new Point(oldConPoint.getX() + dX,
                                            oldConPoint.getY() + dY);
        
        log.debug("newEndPoint=" + newEndPoint);
        
        this.pin.setEndPoint(newEndPoint);
        this.pin.setConnectionPoint(newConPoint);
        
        this.symView.repaint();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
            this.symView.getSymbol().addPin(this.pin);
            this.symView.repaint();
            this.pin = null;
            this.symView.setCurrentTool(this.symView.getPanZoomTool());
        }
    }
    
    @Override
    @SuppressWarnings(value={"BC_UNCONFIRMED_CAST"})
    public void paintOverlay(final Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        ShapePainter.paint(g, this.pin);
    }
    
    private final SymbolView symView;
    private Pin pin;
    public static final long serialVersionUID = 1L;
}
