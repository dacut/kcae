package kanga.kcae.view.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.BaseUnit;
import kanga.kcae.object.EngFormatter;
import kanga.kcae.object.Extents;

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.log10;
import static java.lang.Math.max;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.round;
import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.SwingConstants.VERTICAL;

public class Ruler extends JPanel {
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(Ruler.class);
    private static final long serialVersionUID = 5770541034442330291L;

    public static final int MARGIN = 1;
    public static final Color BACKGROUND_COLOR = WHITE;
    public static final Color FOREGROUND_COLOR = BLACK;
    public static final double DESIRED_TICK_SPACING = 250.0;
    public static final double R_DESIRED_TICK_SPACING =
            1.0 / DESIRED_TICK_SPACING;

    public Ruler(final int orientation, final BaseUnit baseUnit) {
        this(0L, 0L, orientation, baseUnit);
    }

    public Ruler(
        final long min,
        final long max,
        final int orientation,
        final BaseUnit baseUnit)
    {
        super(true);
        this.min = min;
        this.max = max;
        this.setOrientation(orientation);
        this.setBaseUnit(baseUnit);
        this.font = new Font(Font.SANS_SERIF, Font.PLAIN, 9);
        return;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2 = (Graphics2D) g;
        final Rectangle bounds = this.getBounds(null);
        final int orientation = this.getOrientation();
        final int width = (orientation == HORIZONTAL ?
                           bounds.width : bounds.height);
        final int height = (orientation == HORIZONTAL ?
                            bounds.height : bounds.width);
        final Extents.Double extents = this.getUserExtents();
        final double start = extents.min;
        final double end = extents.max;
        final double range = end - start;
        final FontMetrics fontMetrics;
        final double tickSpacing = this.getMajorTickSpacing(width);
        final int tickSpacingFigs = (int) floor(log10(tickSpacing));
        
        g2.setColor(BACKGROUND_COLOR);
        if (orientation != HORIZONTAL) {
            g2.translate(0, bounds.height);
            g2.rotate(- 0.5 * PI);
        }
        g2.fill(new Rectangle2D.Double(0, 0, width, height));

        g2.setColor(FOREGROUND_COLOR);
        g2.setFont(this.font);
        fontMetrics = g2.getFontMetrics();
        g2.draw(new Line2D.Double(0, height - 2, width, height - 2));
        
        // Draw the ticks.
        for (double tick = (ceil(start / tickSpacing) - 1.0) * tickSpacing;
             tick <= end + tickSpacing;
             tick += tickSpacing)
        {
            final int offset = (int) ((tick - start) * width / range);
            final int x = (orientation == HORIZONTAL ? offset : width - offset);
            final int sigFigs = max(1, ((int) floor(log10(abs(tick)))) -
                                       tickSpacingFigs + 1);
            final String tickLabel = EngFormatter.format(
                tick, sigFigs, this.getBaseUnit().unitName);
            final Rectangle2D labelBounds = fontMetrics.getStringBounds(
                tickLabel, g);
            final int labelWidth = (int) labelBounds.getWidth();
            final int labelHeight = (int) labelBounds.getHeight();
            
            g2.draw(new Line2D.Double(x, 2, x, height - 2));

            // Center the label over the line.
            final int labelx = x - labelWidth / 2;
            final int labely = height - 7;
            
            // Erase the background.
            g2.setColor(BACKGROUND_COLOR);
            g2.fill(new Rectangle2D.Double(
                        labelx - 2, labely - labelHeight + 1,
                        labelWidth + 4, labelHeight + 2));                    
            g2.setColor(FOREGROUND_COLOR);
            g2.drawString(tickLabel, labelx, labely);                
        }

        return;
    }

    @Override
    public Dimension getPreferredSize() {
        if (this.getOrientation() == HORIZONTAL) {
            return new Dimension(Integer.MAX_VALUE, 20);
        } else {
            return new Dimension(20, Integer.MAX_VALUE);
        }
    }

    public double getMajorTickSpacing(final int width) {
        final double range = this.getRange();

        if (range <= 0) {
            // Degenerate case -- no ruler possible.
            return 0;
        }

        // Try to get one tick every DESIRED_TICK_SPACING pixels.
        final double desiredTicks = R_DESIRED_TICK_SPACING * (double) width;

        // What spacing does this indicate given our range?
        final double perfectRangeSpacing = range / desiredTicks;

        // perfectRangeSpacing is likely an irregular number, e.g. 473.70.
        // Normalize this to a value between [1.0, 10.0).  In our example,
        // rangeScale == 2.0, normalizedRangeSpacing == 4.7370.
        final int rangeScale = (int) floor(log10(perfectRangeSpacing));
        final double normalizedRangeSpacing =
                perfectRangeSpacing / pow(10.0, rangeScale);

        // Move this to a number a human can interpret on a scale.  We allow
        // 1, 2, 5, and 10 here.  In our example, bestSpacing == 5,
        // bestDiff == 5.0 - 4.7370 == 0.2630
        double bestSpacing = 1.0;
        double diff = abs(normalizedRangeSpacing - 1.0);
        double bestDiff = diff;

        if ((diff = abs(normalizedRangeSpacing - 2.0)) < bestDiff) {
            bestSpacing = 2;
            bestDiff = diff;
        }
        if ((diff = abs(normalizedRangeSpacing - 5.0)) < bestDiff) {
            bestSpacing = 5;
            bestDiff = diff;
        }
        if ((diff = abs(normalizedRangeSpacing - 10.0)) < bestDiff) {
            bestSpacing = 10;
            bestDiff = diff;
        }

        // Scale this back up to our denormalized range.  In our example,
        // bestSpacing == 500.0.
        if (rangeScale > 0) {
            for (int denormCount = rangeScale; denormCount > 0; --denormCount) {
                bestSpacing *= 10.0;
            }
        }
        else if (rangeScale < 0) {
            for (int denormCount = rangeScale; denormCount < 0; ++denormCount) {
                bestSpacing *= 0.1;
            }
        }

        return bestSpacing;
    }

    public int getOrientation() {
        return this.orientation;
    }

    public void setOrientation(final int orientation) {
        if (orientation != HORIZONTAL && orientation != VERTICAL) {
            throw new IllegalArgumentException(
                    "orientation must be HORIZONTAL or VERTICAL.");
        }

        this.orientation = orientation;
    }

    public BaseUnit getBaseUnit() {
        return this.baseUnit;
    }

    public void setBaseUnit(BaseUnit baseUnit) {
        this.baseUnit = baseUnit;
    }

    /** Returns the range of the ruler in user units. */
    public double getRange() {
        return this.getBaseUnit().nanometersToUnits(this.max - this.min);
    }
    
    /** Returns the extents of the ruler in user units. */
    public Extents.Double getUserExtents() {
        final BaseUnit bu = this.getBaseUnit();
        
        return new Extents.Double(
            bu.nanometersToUnits(this.min),
            bu.nanometersToUnits(this.max));
    }
    
    /** Sets the extents of the ruler in user units. */
    public void setUserExtents(final Extents.Double extents) {
        final BaseUnit bu = this.getBaseUnit();
        
        this.min = bu.unitsToNanometers(extents.min);
        this.max = bu.unitsToNanometers(extents.max);
        this.repaint();
    }
    
    /** Returns the extents of the ruler in quanta. */
    public Extents.Long getQuantaExtents() {
        return new Extents.Long(this.min, this.max);
    }

    /** Sets the extents of the ruler in quanta. */
    public void setQuantaExtents(final Extents.Long extents) {
        this.min = extents.min;
        this.max = extents.max;
        this.repaint();
    }
    
    /** Returns the number of quanta pixel represents. */
    public long getQuantaPerPixel() {
        final long range = this.max - this.min;
        final long dimension = 
            this.getOrientation() == HORIZONTAL ?
            this.getWidth() : this.getHeight();
        return round(((double) range) / ((double) dimension));
    }

    private long min;
    private long max;
    private int orientation;
    private BaseUnit baseUnit;
    private Font font;
}