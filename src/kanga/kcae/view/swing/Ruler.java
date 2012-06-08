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

import static java.awt.Color.BLACK;
import static java.awt.Color.WHITE;
import static java.lang.Math.abs;
import static java.lang.Math.ceil;
import static java.lang.Math.floor;
import static java.lang.Math.log10;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.SwingConstants.VERTICAL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Ruler extends JPanel {
    private static final Log log = LogFactory.getLog(Ruler.class);
    private static final Log fmtlog = LogFactory.getLog(
        Ruler.class.getName() + ".format");
    private static final long serialVersionUID = 5770541034442330291L;

    public static final long MM = 1000000;
    public static final long INCH = 25400000;

    public static final int MARGIN = 1;
    public static final Color BACKGROUND_COLOR = WHITE;
    public static final Color FOREGROUND_COLOR = BLACK;
    public static final double DESIRED_TICK_SPACING = 250.0;
    public static final double R_DESIRED_TICK_SPACING =
            1.0 / DESIRED_TICK_SPACING;

    public Ruler(final int orientation) {
        this(0L, 0L, orientation, MM);
    }

    public Ruler(final long min, final long max, final int orientation) {
        this(min, max, orientation, MM);
    }

    public Ruler(final long min, final long max, final int orientation,
            final long baseUnit) {
        super(true);
        this.min = min;
        this.max = max;
        this.setOrientation(orientation);
        this.setBaseUnit(baseUnit);
        this.font = new Font(Font.SANS_SERIF, Font.PLAIN, 8);
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
        final double start = this.getStart();
        final double end = this.getEnd();
        final FontMetrics fontMetrics;
        final double tickSpacing = this.getMajorTickSpacing(width);

        log.debug("Ruler.paintComponent(" + bounds + ") start=" + start + " end=" + end);
        g2.setColor(BACKGROUND_COLOR);
        if (orientation != HORIZONTAL) {
            g2.translate(0, bounds.height);
            g2.rotate(- 0.5 * PI);
            log.debug("translate/rotate");
        }
        g2.fill(new Rectangle2D.Double(0, 0, width, height));

        g2.setColor(FOREGROUND_COLOR);
        g2.setFont(this.font);
        fontMetrics = g2.getFontMetrics();
        g2.draw(new Line2D.Double(0, height - 2, width, height - 2));
        
        // Draw the ticks.
        for (double tick = (ceil(start / tickSpacing) - 1.0) * tickSpacing;
             tick < end + tickSpacing; tick += tickSpacing)
        {
            final int offset = (int) ((tick - start) * width / this.getRange());
            final int x = (orientation == HORIZONTAL ? offset : width - offset);
            final String tickLabel = formatTick(tick);
            final Rectangle2D labelBounds = fontMetrics.getStringBounds(
                tickLabel, g);
            final int labelWidth = (int) labelBounds.getWidth();
            final int labelHeight = (int) labelBounds.getHeight();
            
            g2.draw(new Line2D.Double(x, 2, x, height - 2));

            // Center the label over the line.
            int labelx = x - labelWidth / 2;
            if (labelx < 0)
                labelx = 0;
            else if (labelx + labelWidth > width)
                labelx = width - labelWidth;
            final int labely = height - 7;
            
            log.debug("tick=" + tick + "; offset=" + offset + "; x=" + x + "; tickLabel=" + tickLabel + "; " +
                      "labelx=" + labelx + "; labely=" + labely);
                
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

        fmtlog.debug("getMajorTickSpacing: range=" + range + "; min=" +
                     this.getMin() + "; max=" + this.getMax());

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

        fmtlog.debug("desiredTicks=" + desiredTicks + "; perfectRangeSpacing=" +
                     perfectRangeSpacing + "; rangeScale=" + rangeScale +
                     "; normalizedRangeSpacing=" + normalizedRangeSpacing);      

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

        fmtlog.debug("Before denormalization: bestSpacing=" + bestSpacing +
                     "; bestDiff=" + bestDiff);

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

        fmtlog.debug("After denormalization: bestSpacing=" + bestSpacing);

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

    public long getBaseUnit() {
        return this.baseUnit;
    }

    public void setBaseUnit(long baseUnit) {
        if (baseUnit <= 0) {
            throw new IllegalArgumentException("baseUnit must be >= 0.");
        }

        this.baseUnit = baseUnit;
        this.rBaseUnit = 1.0 / (double) baseUnit;
    }

    /** Returns the range of the ruler in user units. */
    public double getRange() {
        return ((double) (this.max - this.min)) * this.rBaseUnit;
    }

    /** Returns the left/top edge of the ruler in user units. */
    public double getStart() {
        return ((double) this.min) * this.rBaseUnit;
    }

    /** Sets the left/top edge of the ruler in user units. */
    public void setStart(final double start) {
        this.min = (long) (start * this.baseUnit);
    }

    /** Returns the right/bottom edge of the ruler in user units. */
    public double getEnd() {
        return ((double) this.max) * this.rBaseUnit;
    }

    /** Sets the right/bottom edge of the ruler in user units. */
    public void setEnd(final double end) {
        this.max = (long) (end * this.baseUnit);
    }

    /** Returns the left/top edge of the ruler in quanta. */
    public long getMin() {
        return this.min;
    }

    /** Sets the left/top edge of the ruler in quanta. */
    public void setMin(final long min) {
        this.min = min;
    }

    /** Returns the right/bottom edge of the ruler in quanta. */
    public long getMax() {
        return this.max;
    }

    /** Sets the right/bottom edge of the ruler in quanta. */
    public void setMax(final long max) {
        this.max = max;
    }

    private static char engPrefixes[] = {
        'p', 'n', 'u', 'm', '\0', 'k', 'M', 'G', 'T',
    };
    
    public static String formatTick(final double tick) {
        final double absTick = abs(tick);
        
        if (absTick < 1e-12) {
            return "0";
        }

        // Interpret this on an engineering scale
        final int scale = ((int) floor(log10(absTick) / 3));
        final int engPrefixIdx = scale + 4;
        
        fmtlog.debug("tick=" + tick + "; scale=" + scale);
        
        if (engPrefixIdx < 0) {
            // Smaller than pico
            return String.valueOf(tick * 1e12) + " p";
        }
        else if (engPrefixIdx >= engPrefixes.length) {
            // Greater than tera
            return String.valueOf((int) tick * 1e-12) + " T";
        }
        
        final char prefix = engPrefixes[engPrefixIdx];
        if (prefix == '\0') {
            // No prefix used (0-1000)
            return String.valueOf((int) tick);
        }
        else {
            return String.valueOf((int) (tick * pow(10.0, -scale * 3))) +
                    ' ' + prefix;
        }
    }

    private long min;
    private long max;
    private int orientation;
    private long baseUnit;
    private double rBaseUnit;
    private Font font;
}