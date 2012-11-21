package kanga.kcae.xchg.autocad;

import java.io.Serializable;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/** The abstract base class for AutoCAD shape directives.
 * 
 *  <p>All shape instructions must override the
 *  {@link #visit(ShapeInstructionHandler)} method.  This is a visitor pattern
 *  enabling double dispatch for {@link ShapeInstructionHandler} objects.</p>
 */
public abstract class ShapeInstruction implements Serializable {
    /** Create a new shape instruction.
     * 
     *  @param verticalOnly If true, this instruction applies only when the
     *         shape is being drawn in a vertical orientation.
     */
    public ShapeInstruction(boolean verticalOnly) {
        this.verticalOnly = verticalOnly;
    }
    
    /** Indicates whether the directive is active only when the shape is
     *  being drawn in a vertical orientation.
     */
    public boolean verticalOnly() {
        return this.verticalOnly;
    }
    
    /** Invoked by the shape instruction handler to perform double dispatch.
     * 
     *  <p>All shape instructions must override this method as:
     *<pre>@Override
     *public void visit(ShapeInstructionHandler handler) {
     *    handler.handle(this);
     *}</pre>
     * 
     *  @param handler The shape instruction handler interpreting the shape.
     */
    public abstract void visit(ShapeInstructionHandler handler);
    
    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (this == otherObj) { return true; }
        if (this.getClass() != otherObj.getClass()) { return false; }
     
        ShapeInstruction other = ShapeInstruction.class.cast(otherObj);
        return this.verticalOnly() == other.verticalOnly();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(189235349, 646401533)
            .append(this.verticalOnly())
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return this.toStringBuilder().toString();
    }
    
    protected ToStringBuilder toStringBuilder() {
        ToStringBuilder sb = new ToStringBuilder(this, SHORT_PREFIX_STYLE);
        
        if (this.verticalOnly()) {
            sb.appendToString("verticalOnly");
        }
        
        return sb;
    }
    
    private final boolean verticalOnly;
    private static final long serialVersionUID = 1L;
}
