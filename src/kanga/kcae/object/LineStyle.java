package kanga.kcae.object;

import java.util.Arrays;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

public class LineStyle {
    public enum CapStyle {
        butt, round, square;
    }
    
    public enum JoinStyle {
        bevel, miter, round;
    }

    public LineStyle() {
        this(0, null, null, CapStyle.butt, JoinStyle.bevel, 1000);
    }

    public LineStyle(final int width, final Color color) {
        this(width, color, null, CapStyle.butt, JoinStyle.bevel, 1000);
    }

    public LineStyle(
        final int width,
        final Color color,
        final int[] stipple,
        final CapStyle capStyle,
        final JoinStyle joinStyle,
        final int miterLimit)
    {
        this.setWidth(width);
        this.setColor(color);
        this.setStipple(stipple);
        this.setCapStyle(capStyle);
        this.setJoinStyle(joinStyle);
        this.setMiterLimit(miterLimit);
        return;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(final int width) {
        this.width = width;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    @SuppressWarnings("PZLA_PREFER_ZERO_LENGTH_ARRAYS")
    public int[] getStipple() {
        if (this.stipple == null) { return null; }
        return Arrays.copyOf(this.stipple, this.stipple.length);
    }

    public void setStipple(final int[] stipple) {
        if (stipple == null) {
            this.stipple = null;
        }
        else {
            final int[] newStipple = Arrays.copyOf(stipple, stipple.length);
            for (int i = 0; i < newStipple.length; ++i) {
                if (newStipple[i] <= 0) {
                    throw new IllegalArgumentException(
                        "Stipple elements must be positive (stipple[" + i +
                        "] = " + newStipple[i] + ").");
                }
            }

            this.stipple = newStipple;
        }

        return;
    }

    public CapStyle getCapStyle() {
        return this.capStyle;
    }

    public void setCapStyle(final CapStyle capStyle) {
        if (capStyle == null) {
            throw new NullPointerException("capStyle cannot be null.");
        }

        this.capStyle = capStyle;
    }

    public JoinStyle getJoinStyle() {
        return this.joinStyle;
    }

    public void setJoinStyle(final JoinStyle joinStyle) {
        if (joinStyle == null) {
            throw new NullPointerException("joinStyle cannot be null.");
        }

        this.joinStyle = joinStyle;
    }

    public int getMiterLimit() {
        return this.miterLimit;
    }

    public void setMiterLimit(int miterLimit) {
        if (miterLimit < 1000) {
            throw new NullPointerException(
                "miterLimit must be greater than 1000.");
        }

        this.miterLimit = miterLimit;
    }

    @Override
    public String toString() {
        return ("LineStyle(" + this.getWidth() + ", " +
                String.valueOf(this.getColor()) + ", " +
                Arrays.toString(this.stipple) + ", " +
                this.getCapStyle() + ", " +
                this.getJoinStyle() + ", " +
                this.getMiterLimit() + ")");
    }

    private int width;
    private Color color;
    private int[] stipple;
    private CapStyle capStyle;
    private JoinStyle joinStyle;
    private int miterLimit;
}