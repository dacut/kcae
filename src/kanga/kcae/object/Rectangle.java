package kanga.kcae.object;

import static java.lang.Math.min;
import static java.lang.Math.max;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/** A rectangle in Cartesian 2-D space whose corners can be specified to the
 *  nearest nanometer.
 * 
 *  @see Point
 */
public final class Rectangle implements Comparable<Rectangle> {
	/** Create a new rectangle.
	 * 	
	 * 	This constructor assumes that ({@code x0}, {@code y0}) represents
	 *  the upper-left corner and ({@code x1}, {@code y1}) represents the
	 *  lower-right corner.  No checking is performed.
	 *  
	 *  @param left	The horizontal position of the left edge of the rectangle,
	 *  			in nanometers.
	 *  @param top	The vertical position of the top edge of the rectangle,
	 *  			in nanometers.
	 *  @param right   The horizontal position of the right edge of the rectangle,
	 *  			in nanometers.
	 *  @param bottom	The vertical position of the bottom edge of the rectangle,
	 *  			in nanometers.
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
	 *  @param x0	The horizontal position of one corner of the rectangle, in
	 *  			nanometers.
	 *  @param y0	The vertical position of one corner of the rectangle, in
	 *  			nanometers.
	 *  @param x1	The horizontal position of the opposite corner of the
	 *  			rectangle, in nanometers.
	 *  @param y1	The vertical position of the opposite corner of the
	 *  			rectangle, in nanometers.
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
    public static Rectangle fromPoints(Point p1, Point p2) {
    	if (p1 == null) {
    		throw new NullPointerException("p1 cannot be null");
    	}
    	
    	if (p2 == null) {
    		throw new NullPointerException("p2 cannot be null");
    	}
    	
        return new Rectangle(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }
    
    /** Find the smallest rectangle encompassing this and another rectangle.
     * 
     *  @param other	The other rectangle to encompass.
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
     *  @param other	The point to encompass.
     *  @return	The smallest rectangle encompassing this rectangle and the
     *  		point {@code other}.
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

    private final long left;
    private final long right;
    private final long top;
    private final long bottom;
}