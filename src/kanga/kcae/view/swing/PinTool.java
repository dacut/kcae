package kanga.kcae.view.swing;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import static java.lang.Character.isISOControl;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import kanga.kcae.object.Pin;
import kanga.kcae.object.Point;
import kanga.kcae.object.SignalDirection;

import static org.apache.commons.lang3.StringUtils.defaultString;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Tool for creating a new pin on a symbol.
 */
public class PinTool
    extends MeasuredViewToolAdapter
    implements KeyEventDispatcher
{
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(PinTool.class);
    
    public PinTool(final SymbolView symView) {
        this.symView = symView;
        this.pin = null;
    }
    
    @Override
    public void enabled() {
        this.pin = new Pin(null, (String) null, SignalDirection.PASSIVE,
                           new Point(-30000000, 0), new Point(0, 0));
        this.symView.setCursor(
                Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));

        // Don't allow the symbol viewer to lose focus when the user hits the
        // tab key.
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .addKeyEventDispatcher(this);
    }

    @Override
    public void disabled() {
        this.symView.setCursor(
            Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

        // Stop trapping the tab key.
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
            .removeKeyEventDispatcher(this);
    }
    
    @Override
    public boolean dispatchKeyEvent(final KeyEvent evt) {
        // Don't allow the TAB key to be dispatched.  (Returning true tells
        // the KeyboardFocusManager that the event was handled.)
        return (evt.getKeyCode() == KeyEvent.VK_TAB);
    }
    
    @Override
    public void mouseMoved(final MouseEvent e) {
        final Point oldEndPoint = this.pin.getEndPoint();
        final Point oldConPoint = this.pin.getConnectionPoint();
        final Point newEndPoint = this.symView.roundToGrid(
            this.symView.screenPointToQuanta(e.getPoint()));
        final long dX = newEndPoint.getX() - oldEndPoint.getX();
        final long dY = newEndPoint.getY() - oldEndPoint.getY();
        final Point newConPoint = new Point(oldConPoint.getX() + dX,
                                            oldConPoint.getY() + dY);
        
        this.pin.setEndPoint(newEndPoint);
        this.pin.setConnectionPoint(newConPoint);
        
        this.symView.repaint();
    }
    
    @Override
    public void mouseClicked(final MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 1) {
            if (this.symView.hasFocus()) {
                this.symView.getSymbol().addPin(this.pin);
                this.pin = null;
                this.symView.repaint();
                this.symView.setCurrentTool(this.symView.getPanZoomTool());
            } else {
                this.symView.requestFocus();
            }
        }
    }
    
    @Override
    public void keyPressed(final KeyEvent e) {
        int c = e.getKeyCode();
        
        if (c == KeyEvent.VK_TAB) {
            this.editName = !this.editName;
        }
        else if (c == KeyEvent.VK_ESCAPE) {
            // Escape -- quit this tool without adding the pin.
            this.pin = null;
            this.symView.repaint();
            this.symView.setCurrentTool(this.symView.getPanZoomTool());
        }
        else {
            return;
        }
        
        e.consume();
    }
    
    @Override
    public void keyTyped(final KeyEvent e) {
        char c = e.getKeyChar();
        
        if (c == '\u0009') {
            // Tab -- toggle between name and number input. 
            this.editName = !this.editName;
        }
        else if (c == '\u0008') {
            // Backspace -- delete the previous character.
            String text = defaultString(this.editName ? this.pin.getName() :
                                        this.pin.getPinNumber());
            if (text.length() > 0) {
                text = text.substring(0, text.length() - 1);
                if (this.editName) {
                    this.pin.setName(text);
                } else {
                    this.pin.setPinNumber(text);
                }
                
                this.symView.repaint();
            }
        }
        else if (c == '\u001b') {
            // Escape -- quit this tool without adding the pin.
            this.pin = null;
            this.symView.repaint();
            this.symView.setCurrentTool(this.symView.getPanZoomTool());
        }
        else if (! isISOControl(c)) {
            String text = defaultString(this.editName ? this.pin.getName() :
                                        this.pin.getPinNumber());
            text = text + c;
            if (this.editName) {
                this.pin.setName(text);
            } else {
                this.pin.setPinNumber(text);
            }

            this.symView.repaint();
        }
        
        // Don't propagate keypresses outside of the viewer.
        e.consume();
    }
    
    @Override
    @SuppressWarnings(value={"BC_UNCONFIRMED_CAST"})
    public void paintOverlay(final Graphics graphics) {
        Graphics2D g = (Graphics2D) graphics;
        PinPainter.paint(this.symView.getSymbol(), this.pin, g);
    }

    private final SymbolView symView;
    private Pin pin;
    private boolean editName = true;
    public static final long serialVersionUID = 1L;
}