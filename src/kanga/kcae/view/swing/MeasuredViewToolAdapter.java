package kanga.kcae.view.swing;

import java.awt.Graphics;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.io.Serializable;

public class MeasuredViewToolAdapter implements MeasuredViewTool, Serializable {
    @Override
    public void focusGained(final FocusEvent e) { }

    @Override
    public void focusLost(FocusEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseEntered(MouseEvent e) { }

    @Override
    public void mouseExited(MouseEvent e) { }

    @Override
    public void mouseDragged(MouseEvent e) { }

    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) { }
    
    @Override
    public void enabled() { }
    
    @Override
    public void disabled() { }
    
    @Override
    public void paintBackground(Graphics g) { }

    @Override
    public void paintOverlay(Graphics g) { }

    private static final long serialVersionUID = 1L;
}
