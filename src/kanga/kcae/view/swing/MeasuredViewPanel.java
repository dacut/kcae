package kanga.kcae.view.swing;

import static java.lang.Math.round;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.BaseUnit;
import kanga.kcae.object.Point;
import kanga.kcae.object.Rectangle;

public abstract class MeasuredViewPanel extends JPanel implements MeasuredView {
    private static final Log log = LogFactory.getLog(MeasuredViewPanel.class);
    private static final long serialVersionUID = -4832792841867215854L;
    
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
        this.setViewArea(viewArea);
        this.setBaseUnit(baseUnit);
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
        log.debug("setViewArea: " + viewArea);
        this.viewArea = viewArea;
        this.repaint();
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
    
    private Rectangle viewArea;
    private BaseUnit baseUnit;
    protected Color backgroundColor;
}
