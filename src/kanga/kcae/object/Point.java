package kanga.kcae.object;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/** A point in Cartesian 2-D space.
 * 
 *  <p>In this system, X and Y are nanometer-precision values represented as
 *  64-bit integers (Java longs).</p>
 *  
 *  <p>Nanometers (instead of micrometers/microns) were chosen as the base unit
 *  to allow for milliinch-precision values (25.4 microns) to be represented in
 *  a metric system.  The choice of 64-bit (instead of 32-bit) values was made
 *  to enable mechanical descriptions in excess of 2 meters on an edge to be
 *  represented.  (The physical limit at 64-bits is roughly one light-year.)</p>
 *  
 *  <p>Points are immutable and thread-safe.</p>
 */
public final class Point implements Comparable<Point>, Serializable {
    /** Create a new point at the specified coordinates.
     * 
     *  @param x	The x (or horizontal) coordinate, in nanometers.
     *  @param y	The y (or vertical) coordinate, in nanometers.
     */
    public Point(final long x, final long y) {
        this.x = x;
        this.y = y;
    }

    /** Return the x (or horizontal) coordinate, in nanometers.
     * 
     *  @return	The x (or horizontal) coordinate, in nanometers.
     */
    public long getX() { return this.x; }
    
    /** Return the y (or vertical) coordinate, in nanometers.
     * 
     *  @return	The y (or vertical) coordinate, in nanometers.
     */    
    public long getY() { return this.y; }

    /** Return the smallest rectangle encompassing both this and another point.
     * 
     *  @param other	The other point. 
     *  @return	The smallest rectangle enclosing this and the other point.
     *  @throws NullPointerException if {@code other} is null.
     */
    public Rectangle union(final Point other) {
        if (other == null) {
            throw new NullPointerException("other cannot be null.");
        }

        return new Rectangle(this.getX(), this.getY(),
                             other.getX(), other.getY());
    }

    /** Indicates whether this point represents the same coordinate as another.
     *
     *	@param otherObj	The object to compare against.
     *	@return {@code true} if {@code otherObj} is a non-null {@code Point}
     *  		and represents the same coordinate as this point; {@code false}
     *  		otherwise.
     */
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

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        return new HashCodeBuilder(36409, 62573)
            .append(this.x)
            .append(this.y)
            .toHashCode();
    }

    /** Orders two {@code Point} objects.
     * 
     *  The ordering algorithm simply compares the x and y coordinates.
     * 
     *  @param other	The other point to order this point against.
     *  @return The return value indicates the sorting order of this point
     *  		relative to {@code other} with -1 indicating this before other,
     *  		+1 indicating this after other, and 0 indicating the two points
     *  		represent the same coordinate.
     */
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

    /** Return a textual description of the point
     * 
     *  @return A string in the form {@code "Point(x, y)"}.
     */
    @Override
    public String toString() {
        return "Point(" + this.x + ", " + this.y + ")";
    }

    private final long x;
    private final long y;
    private static final long serialVersionUID = 1L;
}