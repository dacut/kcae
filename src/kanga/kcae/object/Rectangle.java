package kanga.kcae.object;

public class Rectangle {
    public Rectangle(
        final long x0, 
        final long y0,
        final long x1,
        final long y1)
    {
        this(new Point(x0, y0), new Point(x1, y1));
    }

    public Rectangle(final Point start, final Point end) {
        if (start == null) {
            throw new NullPointerException("start cannot be null.");
        }
        if (end == null) {
            throw new NullPointerException("end cannot be null.");
        }

        this.start = start;
        this.end = end;
    }

    @Override
    public String toString() {
        return "Rectangle(" + this.start + ", " + this.end + ")";
    }

    public final Point start;
    public final Point end;
}