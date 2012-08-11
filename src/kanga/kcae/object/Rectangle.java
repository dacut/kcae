package kanga.kcae.object;

import static java.lang.Math.min;
import static java.lang.Math.max;
import static java.lang.Math.round;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/** A rectangle in Cartesian 2-D space whose points lie on integral nanometer
 *  quanta.
 * 
 *  <p>Rectangles are immutable and thread-safe.</p>
 *  
 *  @see Point
 */
public final class Rectangle implements Comparable<Rectangle> {
    /** How the fitting algorithm should work. */
    public static enum FitMethod {
        /** Fit to the rectangle most closely matching this one. */
        NEAREST,
        
        /** Shrink the rectangle. */
        SHRINK,
        
        /** Expand the rectangle. */
        EXPAND;
    }
    
    /** Create a new rectangle.
     * 	
     * 	This constructor assumes that ({@code x0}, {@code y0}) represents
     *  the upper-left corner and ({@code x1}, {@code y1}) represents the
     *  lower-right corner.  No checking is performed.
     *  
     *  @param left     The horizontal position of the left edge of the
     *                  rectangle, in quanta (nanometers).
     *  @param top      The vertical position of the top edge of the rectangle,
     *                  in quanta (nanometers).
     *  @param right    The horizontal position of the right edge of the
     *                  rectangle, in quanta (nanometers).
     *  @param bottom	The vertical position of the bottom edge of the
     *                  rectangle, in quanta (nanometers).
     */
    Rectangle(
        final long left,
        final long top,
        final long right,
        final long bottom)
    {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
    }

    /** Create a new rectangle.
     *
     *  @param x0	The horizontal position of one corner of the rectangle,
     *                  in quanta (nanometers).
     *  @param y0	The vertical position of one corner of the rectangle,
     *                  in quanta (nanometers).
     *  @param x1	The horizontal position of the opposite corner of the
     *                  rectangle, in quanta (nanometers).
     *  @param y1	The vertical position of the opposite corner of the
     *  		rectangle, in quanta (nanometers).
     */
    public static Rectangle fromPoints(
        final long x0,
        final long y0,
        final long x1,
        final long y1)
    {
        return new Rectangle(min(x0, x1), min(y0, y1),
                             max(x0, x1), max(y0, y1));
    }

    /** Create a new rectangle.
     * 
     *  @param p1	One corner of the rectangle.
     *  @param p2	The opposite corner of the rectangle.
     *  @throws NullPointerException if {@code p1} or {@code p2} is
     *  		{@code null}.
     */
    public static Rectangle fromPoints(final Point p1, final Point p2) {
        if (p1 == null) {
            throw new NullPointerException("p1 cannot be null");
        }

        if (p2 == null) {
            throw new NullPointerException("p2 cannot be null");
        }

        return Rectangle.fromPoints(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
    
    /** Create a new rectangle.
     * 
     *  @param hExtents     The horizontal extents of the rectangle.
     *  @param vExtents     The vertical extents of the rectangle.
     *  @throws NullPointerException if either {@code hExtents} or
     *              {@code vExtents} is {@code null}.  
     */
    public static Rectangle fromExtents(
        final Extents.Long hExtents,
        final Extents.Long vExtents)
    {
        if (hExtents == null) {
            throw new NullPointerException("hExtents cannot be null");
        }
        
        if (vExtents == null) {
            throw new NullPointerException("vExtents cannot be null");
        }
        
        return new Rectangle(hExtents.min, vExtents.min,
                             hExtents.max, vExtents.max);
    }
    
    /** Create a new rectangle with the top left corner at the origin and
     *  of the specified size.
     *  
     *  @param size     The size of the rectangle.
     *  @throws NullPointerException if {@code size} is {@code null}.
     */
    public static Rectangle atOrigin(final Dimension size) {
        if (size == null) {
            throw new NullPointerException("size cannot be null");
        }
        
        return new Rectangle(0, 0, size.getWidth(), size.getHeight());
    }

    /** Find the smallest rectangle encompassing this and another rectangle.
     * 
     *  @param other    The other rectangle to encompass.
     *  @return	The smallest rectangle encompassing this and {@code other}.
     *  @throws NullPointerException if {@code other} is {@code null}.
     */
    public Rectangle union(Rectangle other) {
        if (other == null) {
            throw new NullPointerException("other cannot be null.");
        }

        return new Rectangle(min(this.getLeft(), other.getLeft()),
                min(this.getTop(), other.getTop()),
                max(this.getRight(), other.getRight()),
                max(this.getBottom(), other.getBottom()));
    }

    /** Find the smallest rectangle encompassing this rectangle and a point.
     * 
     *  @param other    The point to encompass.
     *  @return	The smallest rectangle encompassing this rectangle and the
     *                  point {@code other}.
     *  @throws NullPointerException if {@code other} is {@code null}.
     */
    public Rectangle union(Point other) {
        if (other == null) {
            throw new NullPointerException("other cannot be null.");
        }

        return new Rectangle(min(this.getLeft(), other.getX()),
                min(this.getTop(), other.getY()),
                max(this.getRight(), other.getX()),
                max(this.getBottom(), other.getY()));
    }
    
    /** Returns the translation of this rectangle by the specified amount.
     * 
     *  @param dx       The horizontal translation.
     *  @param dy       The vertical translation.
     *  
     *  @return This rectangle translated by {@code (dx, dy)}.
     */
    public Rectangle translate(final long dx, final long dy) {
        return new Rectangle(this.left + dx, this.top + dy,
                             this.right + dx, this.bottom + dy);
    }

    @Override
    public String toString() {
        return
                "Rectangle[(" + this.getLeft() + ", " + this.getTop() + "), " +
                "(" + this.getRight() + ", " + this.getBottom() + ")]";
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }

        Rectangle other = (Rectangle) otherObj;
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
    public int compareTo(Rectangle other) {        
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

    /** Returns the position of the left edge of the rectangle, in
     *  nanometers.
     *  
     *  @return The position of the left edge of the rectangle, in nanometers.
     */
    public long getLeft() { return this.left; }

    /** Returns the position of the right edge of the rectangle, in
     *  nanometers.
     *  
     *  @return	The position of the right edge of the rectangle, in nanometers.
     */
    public long getRight() { return this.right; }

    /** Returns the position of the top edge of the rectangle, in nanometers.
     * 
     *  @return The position of the top edge of the rectangle, in nanometers.
     */
    public long getTop() { return this.top; }

    /** Returns the position of the bottom edge of the rectangle, in
     *  nanometers.
     *  
     *  @return The position of the bottom edge of the rectangle, in
     *  		nanometers.
     */
    public long getBottom() { return this.bottom; }

    /** Returns the width of the rectangle, in nanometers.
     * 
     *  @return	The width of the rectangle, in nanometers.
     */
    public long getWidth() { return this.right - this.left; }

    /** Returns the height of the rectangle, in nanometers.
     * 
     *  @return The height of the rectangle, in nanometers.
     */
    public long getHeight() { return this.bottom - this.top; }

    /** Returns the top-left corner of the rectangle.
     * 
     *  @return	The top-left corner of the rectangle.
     */
    public Point getTopLeft() { return new Point(this.left, this.top); }

    /** Returns the top-right corner of the rectangle.
     * 
     *  @return The top-rigth corner of the rectangle.
     */
    public Point getTopRight() { return new Point(this.right, this.top); }

    /** Returns the bottom-left corner of the rectangle.
     * 
     *  @return	The bottom-left corner of the rectangle.
     */    
    public Point getBottomLeft() { return new Point(this.left, this.bottom); }

    /** Returns the bottom-right corner of the rectangle.
     * 
     *  @return	The bottom-right corner of the rectangle.
     */    
    public Point getBottomRight() { return new Point(this.right, this.bottom); }
    
    /** Returns the center of the rectangle.
     *  The returned value is within a quanta of the actual center.
     */
    public Point getCenter() {
        return new Point((this.left + this.right) / 2,
                         (this.top + this.bottom) / 2);
    }
    
    /** Returns the horizontal extents of the rectangle.
     * 
     *  @return The horizontal extents of the rectangle.
     */
    public Extents.Long getHorizontalExtents() {
        return new Extents.Long(this.left, this.right);
    }
    
    /** Returns the vertical extents of the rectangle.
     * 
     *  @return The vertical extents of the rectangle.
     */
    public Extents.Long getVerticalExtents() {
        return new Extents.Long(this.top, this.bottom);
    }
    
    /** Returns the aspect ratio (width to height) of the rectangle.
     * 
     */
    public double getAspectRatio() {
        final long width = this.getWidth();
        final long height = this.getHeight();
        if (height == 0) {
            if (width == 0) {
                return Double.NaN;
            } else {
                return Double.POSITIVE_INFINITY;
            }
        }
        
        return ((double) width) / ((double) height);
    }
    
    /** Return a copy of this rectangle fitted to the specified aspect ratio.
     * 
     *  @param aspectRatio      The aspect ratio (width over height) to fit
     *      this rectangle to.
     *  @param fitMethod        How the rectangle should be adjusted.  This can
     *      be one of the following values:
     *      <ul>
     *        <li>{@link FitMethod#NEAREST}: the rectangle's width and height
     *            are both adjusted by the minimum amount necessary to meet the
     *            aspect ratio.</li>
     *        <li>{@link FitMethod#SHRINK}: either the width or height is
     *            shunk to meet the aspect ratio.</li>
     *        <li>{@link FitMethod#EXPAND}: either the width or height is
     *            expanded to meet the aspect ratio.</li>
     *      </ul>
     *  @return A copy of this rectangle with its coordinates adjusted to meet
     *          the specified aspectRatio (to the nearest quanta).
     */
    public Rectangle adjustAspectRatio(
        final double aspectRatio,
        final FitMethod fitMethod)
    {
        Rectangle result = this; // Eclipse complains if this isn't initialized
        
        switch (fitMethod) {
            case NEAREST:
            result = this.fitToAspect(aspectRatio);
            
            case SHRINK:
            result = this.shrinkToAspect(aspectRatio);
            
            case EXPAND:
            result = this.expandToAspect(aspectRatio);
        }
        
        return result;
    }
    
    /** Return a copy of this rectangle fitted to the specified aspect ratio.
     * 
     *  The fitting algorithm adjusts both the height and width minimally to
     *  meet the desired ratio.
     * 
     *  @param aspectRatio      The aspect ratio (width over height) to fit
     *                          this rectangle to.
     *  @return A copy of this rectangle with its coordinates adjusted to meet
     *          the specified aspectRatio (to the nearest quanta).
     */
    public Rectangle fitToAspect(final double aspectRatio) {
        /*  Skip this section if you're not interested in the mathematical 
         *  details.
         *  
         *  If we were to graph the aspectRatio a on an X-Y plot where the
         *  x coordinate represents the width and the y coordinate represents
         *  the height, the allowable rectangle sizes would fit along a line L
         *  with slope m = 1 / a, intersecting at the origin.
         *  
         *  Our current size is a point P = (w, h) (where w = width,
         *  h = height), which is not likely to be on L.  We want to find a
         *  point P' = (w', h') on L closest to P.  The line L' between P and P'
         *  is the normal to L, with slope -1/m.
         *  
         *   height
         *    |         \         __/ <- L
         *    |     P -> X     __/
         *    |           \ __/
         *    |          __X <--- P'
         *    |       __/   \
         *    |    __/       \
         *    | __/           \  <-- L'
         *    |/               \
         *    +-----------------------------------
         *    
         *  The equations for the two lines are:
         *  L:   y = m x = x / a
         *  L':  (y - h) = (- 1 / m) (x - w)
         *        y      = -a (x - w) + h
         *
         *  By setting these equal, we find the intersection, P':
         *        x / a       = -a (x - w) + h
         *        x / a + a x = a w + h
         *        (a^2 + 1) x = a^2 w + a h
         *        w' = x = (a^2 w + a h) / (a^2 + 1)
         *               = a (a w + h) / (a^2 + 1)
         *        h' = y = w' / a
         */
        final long width = this.getWidth();
        final long height = this.getHeight();
        final Point center = this.getCenter();
        final long xc = center.getX();
        final long yc = center.getY();
        
        final double aspSq = aspectRatio * aspectRatio;
        final double nWidth = (aspSq * width + aspectRatio * height) /
                              (1 + aspSq);
        final double nHeight = nWidth / aspectRatio;
        
        return new Rectangle(
            round(xc - 0.5 * nWidth), round(yc - 0.5 * nHeight),
            round(xc + 0.5 * nWidth), round(yc + 0.5 * nHeight));
    }

    /** Return a copy of this rectangle with one of its dimensions shrunk to
     *  meet the specified aspect ratio.
     * 
     *  @param aspectRatio      The aspect ratio (width over height) to fit
     *                          this rectangle to.
     *  @return A copy of this rectangle with its coordinates adjusted to meet
     *          the specified aspectRatio (to the nearest quanta).
     */
    public Rectangle shrinkToAspect(final double aspectRatio) {
        final double currentAspect = this.getAspectRatio();
        double width, height;
        final Point center = this.getCenter();
        final long xc = center.getX();
        final long yc = center.getY();
        
        if (currentAspect > aspectRatio) {
            // Too wide.  Shrink the width.
            height = this.getHeight();
            width = aspectRatio * height; 
        } else {
            // Too high.  Shrink the height.
            width = this.getWidth();
            height = width / aspectRatio;
        }
        
        return new Rectangle(
            round(xc - 0.5 * width), round(yc - 0.5 * height),
            round(xc + 0.5 * width), round(yc + 0.5 * height));
    }

    /** Return a copy of this rectangle with one of its dimensions expanded to
     *  meet the specified aspect ratio.
     * 
     *  @param aspectRatio      The aspect ratio (width over height) to fit
     *                          this rectangle to.
     *  @return A copy of this rectangle with its coordinates adjusted to meet
     *          the specified aspectRatio (to the nearest quanta).
     */
    public Rectangle expandToAspect(final double aspectRatio) {
        final double currentAspect = this.getAspectRatio();
        double width, height;
        final Point center = this.getCenter();
        final long xc = center.getX();
        final long yc = center.getY();
        
        if (currentAspect > aspectRatio) {
            // Too wide.  Expand the height.
            width = this.getWidth();
            height = width / aspectRatio;
        } else {
            // Too high.  Expand the width.
            height = this.getHeight();
            width = aspectRatio * height; 
        }
        
        return new Rectangle(
            round(xc - 0.5 * width), round(yc - 0.5 * height),
            round(xc + 0.5 * width), round(yc + 0.5 * height));
    }

    /** Returns a new rectangle zoomed by the specified amount.
     *  
     *  The center of the zoomed rectangle is coincident (within a quanta) with
     *  this rectangle.
     */
    public Rectangle zoom(final double factor) {
        final Point center = this.getCenter();
        final long xc = center.getX();
        final long yc = center.getY();
        final long halfWidth = this.getWidth() / 2;
        final long halfHeight = this.getHeight() / 2;
        final long zoomWidth = (long) (factor * halfWidth);
        final long zoomHeight = (long) (factor * halfHeight);
        
        return new Rectangle(xc - zoomWidth, yc - zoomHeight,
                             xc + zoomWidth, yc + zoomHeight);
    }
    
    /** Returns a new rectangle zoomed by the specified amount while keeping an
     *  arbitrary point coincident between this and the zoomed rectangle.
     */
    public Rectangle zoom(final double factor, final Point point) {
        final long xc = point.getX();
        final long yc = point.getY();
        final long xoffset = xc - this.getLeft();
        final long yoffset = yc - this.getTop();
        final long width = this.getWidth();
        final long height = this.getHeight();
        final long zoomWidth = (long) (factor * width);
        final long zoomHeight = (long) (factor * height);
        final double xrel = ((double) xoffset) / ((double) width);
        final double yrel = ((double) yoffset) / ((double) height);
        
        return new Rectangle(xc - (long) (zoomWidth * xrel),
                             yc - (long) (zoomHeight * yrel),
                             xc + (long) (zoomWidth * (1.0 - xrel)),
                             yc + (long) (zoomHeight * (1.0 - yrel)));
    }

    private final long left;
    private final long right;
    private final long top;
    private final long bottom;
}