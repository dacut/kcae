package kanga.kcae.object;

import java.io.Serializable;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

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
public final class Point
    implements Comparable<Point>, Transformable<Point>, Serializable
{
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
    
    /** Return the distance between this point and another point.
     * 
     *  @param other    The other point.
     *  @return The distance between the points, in nanometers.
     *  @throws NullPointerException if {@code other} is @{code null}.
     */
    public double getDistance(@Nonnull final Point other) {
        final long dx = this.getX() - other.getX();
        final long dy = this.getY() - other.getY();
        
        return sqrt(dx * dx + dy * dy);
    }

    /** Return the smallest rectangle encompassing both this and another point.
     * 
     *  @param other	The other point. 
     *  @return	The smallest rectangle enclosing this and the other point.
     *  @throws NullPointerException if {@code other} is {@code null}.
     */
    @Nonnull
    public Rectangle union(@Nonnull Point other) {
        if (other == null) {
            throw new NullPointerException("other cannot be null.");
        }

        return new Rectangle(this.getX(), this.getY(),
                             other.getX(), other.getY());
    }
    
    /** Scales the point by the specified amount from the origin.
     *
     *  @param factor   The amount to scale by.
     *  @return This point, scaled by the specified amount from the origin.
     */
    @Override
    @Nonnull
    public Point scale(double factor) {
        return new Point(round(this.getX() * factor),
                         round(this.getY() * factor));
    }
    
    /** Translates this point by the specified amount in the x and y directions.
     * 
     *  @param  dx       The amount to translate in the x direction.
     *  @param  dy       The amount to translate in the y direction.
     *  @return A copy of this point translated by (dx, dy).  
     */
    @Override
    @Nonnull
    public Point translate(long dx, long dy) {
        return new Point(this.getX() + dx, this.getY() + dy);
    }
    
    /** Rotates this point through the specified number of quadrants.
     * 
     *  Positive values rotate the positive x-axis toward the positive y-axis.
     * 
     *  @param nQuadrants       The number of quadrants to rotate through.
     *  @return A copy of this point rotated by the specified number of
     *          quadrants.
     */
    @Override
    @Nonnull
    public Point rotateQuadrant(int nQuadrants) {
        nQuadrants = nQuadrants % 4;
        if (nQuadrants < 0)
            nQuadrants += 4;
        
        switch (nQuadrants) {
            case 0:
            return this;
            
            case 1:
            return new Point(-this.getY(), this.getX());
            
            case 2:
            return new Point(-this.getX(), -this.getY());
            
            default: // 3
            return new Point(this.getY(), -this.getX());
        }
    }


    /** Indicates whether this point represents the same coordinate as another.
     *
     *	@param otherObj	The object to compare against.
     *	@return {@code true} if {@code otherObj} is a non-null {@code Point}
     *  		and represents the same coordinate as this point; {@code false}
     *  		otherwise.
     */
    @Override
    public boolean equals(@Nullable Object otherObj) {
        if (otherObj == null)                       { return false; }
        if (this == otherObj)                       { return true; }
        if (this.getClass() != otherObj.getClass()) { return false; }

        Point other = Point.class.cast(otherObj);
        return this.x == other.x && this.y == other.y;
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
    public int compareTo(@Nonnull Point other) {
        long xdiff = this.x - other.x;
        if (xdiff < 0)
            return -1;
        else if (xdiff > 0)
            return +1;

        long ydiff = this.y - other.y;
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
    @Nonnull
    public String toString() {
        return "Point(" + this.x + ", " + this.y + ")";
    }

    private final long x;
    private final long y;
    private static final long serialVersionUID = 1L;
}