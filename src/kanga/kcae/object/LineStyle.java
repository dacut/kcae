package kanga.kcae.object;

import java.util.Arrays;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

public class LineStyle {
    public LineStyle() {
        this(0, null, null);
    }

    public LineStyle(final int width, final Color color) {
        this(width, color, null);
    }

    public LineStyle(final int width, final Color color, final int[] stipple) {
        this.setWidth(width);
        this.setColor(color);
        this.setStipple(stipple);
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

    @Override
    public String toString() {
        return ("LineStyle(" + this.getWidth() + ", " +
                String.valueOf(this.getColor()) + ", " +
                Arrays.toString(stipple) + ")");
    }

    private int width;
    private Color color;
    private int[] stipple;
}