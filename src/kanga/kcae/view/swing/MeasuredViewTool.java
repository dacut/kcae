package kanga.kcae.view.swing;

import java.awt.Graphics;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

public interface MeasuredViewTool
    extends FocusListener, KeyListener, MouseListener, MouseMotionListener,
    MouseWheelListener
{
    public void enabled();
    public void disabled();
    public void paintBackground(Graphics g);
    public void paintOverlay(Graphics g);
}
