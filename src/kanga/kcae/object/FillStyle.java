package kanga.kcae.object;

public class FillStyle {
    public FillStyle() {
        this(null);
    }

    public FillStyle(final Color color) {
        this.setColor(color);
        return;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(final Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "FillStyle(" + String.valueOf(this.getColor()) + ")";
    }

    private Color color;
}