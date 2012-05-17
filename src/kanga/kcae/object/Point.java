package kanga.kcae.object;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class Point implements Comparable<Point> {
    public Point(long x, long y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(final Object otherObj) {
        if (otherObj == null) { return false; }

        try {
            final Point other = (Point) otherObj;
            return this.x == other.x && this.y == other.y;
        }
        catch (final ClassCastException e) {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(36409, 62573)
            .append(this.x)
            .append(this.y)
            .toHashCode();
    }

    @Override
    public int compareTo(final Point other) {
        final long xdiff = this.x - other.x;
        if (xdiff < 0)
            return -1;
        else if (xdiff > 0)
            return +1;

        final long ydiff = this.y - other.y;
        if (ydiff < 0)
            return -1;
        else if (ydiff > 0)
            return +1;
        else
            return  0;
    }

    @Override
    public String toString() {
        return "Point(" + this.x + ", " + this.y + ")";
    }

    public long getX() { return this.x; }
    public long getY() { return this.y; }

    private final long x;
    private final long y;
}