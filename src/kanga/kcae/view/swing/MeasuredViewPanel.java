package kanga.kcae.view.swing;

import static java.lang.Math.round;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import javax.swing.JPanel;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import kanga.kcae.object.BaseUnit;
import kanga.kcae.object.Rectangle;

public abstract class MeasuredViewPanel extends JPanel implements MeasuredView {
    private static final long serialVersionUID = -4832792841867215854L;
    
    protected MeasuredViewPanel() {
        this(null, true, Rectangle.fromPoints(0, 0, 1, 1), BaseUnit.meter);
    }

    protected MeasuredViewPanel(final BaseUnit baseUnit) {
        this(null, true, Rectangle.fromPoints(0, 0, 1, 1), baseUnit);
    }

    protected MeasuredViewPanel(
        final LayoutManager layoutManager,
        final boolean isDoubleBuffered,
        final BaseUnit baseUnit)
    {
        this(layoutManager, isDoubleBuffered, Rectangle.fromPoints(0, 0, 1, 1),
             baseUnit);
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
        this.viewArea = viewArea;
    }
    
    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;
        java.awt.Rectangle clip = g.getClipBounds();

        if (clip == null) {
            clip = this.getBounds();
        }

        g.setColor(this.backgroundColor);
        g.fillRect(clip.x, clip.y, clip.width, clip.height);
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
