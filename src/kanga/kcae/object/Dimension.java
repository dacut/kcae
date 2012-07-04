package kanga.kcae.object;

/** A rectangular dimension in 2-D space whose width and height are integral
 *  quanta values (typically nanometers, as used by the rest of KCAE).
 * 
 *  @see Rectangle
 */
public class Dimension {
    public Dimension(final long width, final long height) {
        this.width = width;
        this.height = height;
    }
    
    public long getWidth() {
        return this.width;
    }
    
    public long getHeight() {
        return this.height;
    }
    
    public long getArea() {
        return this.getWidth() * this.getHeight();
    }
    
    private final long width;
    private final long height;
}
