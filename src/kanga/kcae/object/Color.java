package kanga.kcae.object;

public class Color {
    public static final int MIN_VALUE = 0;
    public static final int MAX_VALUE = 255;

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
        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
        this.setAlpha(alpha);
    }

    public int getRed() {
        return this.red;
    }

    public void setRed(final int red) {
        this.red = rangeCheck(red);
    }

    public int getGreen() {
        return this.green;
    }

    public void setGreen(final int green) {
        this.green = rangeCheck(green);
    }

    public int getBlue() {
        return this.blue;
    }

    public void setBlue(final int blue) {
        this.blue = rangeCheck(blue);
    }

    public int getAlpha() {
        return this.alpha;
    }

    public void setAlpha(final int alpha) {
        this.alpha = rangeCheck(alpha);
    }

    public static int rangeCheck(final int value) {
        if (value < MIN_VALUE) { return MIN_VALUE; }
        else if (value > MAX_VALUE) { return MAX_VALUE; }
        else { return value; }
    }

    @Override
    public String toString() {
        return ("Color(" + this.getRed() + ", " + this.getGreen() + ", " +
                this.getBlue() + ", " + this.getAlpha() + ")");
    }

    private int red;
    private int green;
    private int blue;
    private int alpha;
}