package kanga.kcae.object;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/** A rectangular dimension in 2-D space whose width and height are integral
 *  quanta values (nanometers).
 * 
 *  @see Rectangle
 */
public class Dimension implements Serializable {
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
    
    @Override
    public boolean equals(final Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (this.getClass() != otherObj.getClass()) { return false; }
        
        final Dimension other = (Dimension) otherObj;
        
        return new EqualsBuilder()
            .append(this.getWidth(), other.getWidth())
            .append(this.getHeight(), other.getHeight())
            .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(338473, 338707)
            .append(this.getWidth())
            .append(this.getHeight())
            .toHashCode();
    }
    
    private final long width;
    private final long height;
    private static final long serialVersionUID = 1L;
}
