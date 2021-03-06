package kanga.kcae.object;

import java.io.Serializable;
import java.util.Formatter;

public class Color implements Serializable {
    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 255;
    public static final Color black = new Color(
        MIN_VALUE, MIN_VALUE, MIN_VALUE, MIN_VALUE);
    public static final Color white = new Color(
        MAX_VALUE, MAX_VALUE, MAX_VALUE, MIN_VALUE);

    public Color() {
        this(MIN_VALUE, MIN_VALUE, MIN_VALUE, MIN_VALUE);
    }

    public Color(
        final int red,
        final int green,
        final int blue)
    {
        this(red, green, blue, MIN_VALUE);
    }

    public Color(
        final int red,
        final int green,
        final int blue,
        final int alpha)
    {
        this.rgba = (
            (rangeCheck(alpha) << 24) |
            (rangeCheck(red)   << 16) |
            (rangeCheck(green) <<  8) |
            (rangeCheck(blue)));
    }

    public int getRed() {
        return (this.rgba >>> 16) & 0xff;
    }

    public int getGreen() {
        return (this.rgba >>> 8) & 0xff;
    }

    public int getBlue() {
        return this.rgba & 0xff;
    }

    public int getAlpha() {
        return (this.rgba >>> 24) & 0xff;
    }

    public int getRGBA() {
        return this.rgba;
    }

    public static int rangeCheck(final int value) {
        if (value < MIN_VALUE) { return MIN_VALUE; }
        else if (value > MAX_VALUE) { return MAX_VALUE; }
        else { return value; }
    }

    @Override
    public String toString() {
        final Formatter f = new Formatter();
        try {
            final String result = f.format("#%08x", this.getRGBA()).toString();
            return result;
        }
        finally {
            f.close();
        }
    }

    private int rgba;
    private static final long serialVersionUID = 1L;
}