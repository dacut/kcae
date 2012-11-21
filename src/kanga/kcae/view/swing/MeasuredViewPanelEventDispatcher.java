package kanga.kcae.view.swing;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Dispatch events sent to a {@linkplain MeasuredViewPanel} to its current
 *  tool.
 *  
 *  <p>Objects of this class do not automatically register themselves as event
 *  listeners.  This must be performed by the widget or other appropriate
 *  controller.</p>
 */
class MeasuredViewPanelEventDispatcher
    implements FocusListener, KeyListener, MouseListener,
               MouseMotionListener, MouseWheelListener
{
    private static final Log log = LogFactory.getLog(
        MeasuredViewPanelEventDispatcher.class);
    
    /** Create a new event dispatcher.
     * 
     *  @param measuredViewPanel The panel whose current tool is to receive
     *          events.
     */
    MeasuredViewPanelEventDispatcher(MeasuredViewPanel measuredViewPanel) {
        this.measuredViewPanel = measuredViewPanel;
    }
    
    /** Returns the current tool on the {@link MeasuredViewPanel}.
     * 
     *  @return The current tool on the {@link MeasuredViewPanel}.
     */
    public final MeasuredViewTool getCurrentTool() {
        log.debug("measuredViewPanel focusable? " + this.measuredViewPanel.isFocusable() + " hasFocus? " + this.measuredViewPanel.hasFocus());
        return this.measuredViewPanel.getCurrentTool();
    }

    @Override
    public void focusGained(final FocusEvent e) {
        log.debug("focusGained: " + e);
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.focusGained(e);
        }
    }

    @Override
    public void focusLost(final FocusEvent e) {
        log.debug("focusLost: " + e);
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.focusLost(e);
        }
    }

    @Override
    public void keyTyped(final KeyEvent e) {
        log.debug("keyTyped: " + e);
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.keyTyped(e);
        }
    }

    @Override
    public void keyPressed(final KeyEvent e) {
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.keyPressed(e);
        }
    }

    @Override
    public void keyReleased(final KeyEvent e) {
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.keyReleased(e);
        }
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.mouseClicked(e);
        }
    }

    @Override
    public void mousePressed(final MouseEvent e) {
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.mousePressed(e);
        }
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.mouseReleased(e);
        }
    }

    @Override
    public void mouseEntered(final MouseEvent e) {
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.mouseEntered(e);
        }
    }

    @Override
    public void mouseExited(final MouseEvent e) {
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.mouseExited(e);
        }
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.mouseDragged(e);
        }
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.mouseMoved(e);
        }
    }

    @Override
    public void mouseWheelMoved(final MouseWheelEvent e) {
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.mouseWheelMoved(e);
        }
    }

    /** The panel whose current tool is to receive events.
     */
    private final MeasuredViewPanel measuredViewPanel;
}