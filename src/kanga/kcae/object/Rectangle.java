package kanga.kcae.object;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public final class Rectangle implements Comparable<Rectangle> {
    public Rectangle(
        final long x0, 
        final long y0,
        final long x1,
        final long y1)
    {
        long left, right, top, bottom;

        if (x0 < x1) {
            left = x0;
            right = x1;
        } else {
            left = x1;
            right = x0;
        }
        
        if (y0 < y1) {
            top = y0;
            bottom = y1;
        } else {
            top = y1;
            bottom = y0;
        }

        this.left = left;
        this.right = right;
        this.top = top;
        this.bottom = bottom;
        return;
    }

    public Rectangle(final Point p1, final Point p2) {
        this(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    public Rectangle union(final Rectangle other) {
        long myLeft, myRight, myTop, myBottom;
        long otherLeft, otherRight, otherTop, otherBottom;
        long left, right, top, bottom;

        if (other == null) {
            throw new NullPointerException("other cannot be null.");
        }

        myLeft = this.getLeft();
        myRight = this.getRight();
        myTop = this.getTop();
        myBottom = this.getBottom();

        otherLeft = other.getLeft();
        otherRight = other.getRight();
        otherTop = other.getTop();
        otherBottom = other.getBottom();

        if (myLeft < otherLeft) { left = myLeft; }
        else                    { left = otherLeft; }

        if (myRight > otherRight) { right = myRight; }
        else                      { right = otherRight; }

        if (myTop < otherTop) { top = myTop; }
        else                  { top = otherTop; }

        if (myBottom > otherBottom) { bottom = myBottom; }
        else                        { bottom = otherBottom; }

        return new Rectangle(left, top, right, bottom);
    }

    @Override
    public String toString() {
        return
            "Rectangle[(" + this.getLeft() + ", " + this.getTop() + "), " +
            "(" + this.getRight() + ", " + this.getBottom() + ")]";
    }

    @Override
    public boolean equals(final Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }
        
        final Rectangle other = (Rectangle) otherObj;
        return (this.left == other.left &&
                this.right == other.right &&
                this.top == other.top &&
                this.bottom == other.bottom);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(21983, 5149)
            .append(this.left)
            .append(this.right)
            .append(this.top)
            .append(this.bottom)
            .toHashCode();
    }

    @Override
    public int compareTo(final Rectangle other) {
        if      (this.left < other.left)     { return -1; }
        else if (this.left > other.left)     { return +1; }
        else if (this.right < other.right)   { return -1; }
        else if (this.right > other.right)   { return +1; }
        else if (this.top < other.top)       { return -1; }
        else if (this.top > other.top)       { return +1; }
        else if (this.bottom < other.bottom) { return -1; }
        else if (this.bottom > other.bottom) { return +1; }
        else                                 { return 0; }
    }

    public long getLeft() { return this.left; }
    public long getRight() { return this.right; }
    public long getTop() { return this.top; }
    public long getBottom() { return this.bottom; }
    public long getWidth() { return this.right - this.left; }
    public long getHeight() { return this.bottom - this.top; }
    public Point getTopLeft() { return new Point(this.left, this.top); }
    public Point getTopRight() { return new Point(this.right, this.top); }
    public Point getBottomLeft() { return new Point(this.left, this.bottom); }
    public Point getBottomRight() { return new Point(this.right, this.bottom); }

    private final long left;
    private final long right;
    private final long top;
    private final long bottom;
}