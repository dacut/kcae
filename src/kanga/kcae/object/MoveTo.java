package kanga.kcae.object;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MoveTo extends SinglePointInstruction {
    public MoveTo(final long x, final long y) {
        super(new Point(x, y));
    }
    
    public MoveTo(final Point point) {
        super(point);
    }

    @Override
    public void paint(PathPainter pp) {
        pp.moveTo(this.getPoint());
    }
    
    /** Updates the bounding box.
     * 
     *  Since the moveto instruction does not lay down any artifacts, this
     *  just returns the bounding box passed in.
     *  
     *  @param startPos     The current position when the moveTo instruction is
     *                      executed.
     *  @param bbox         The current bounding box.
     *  @return The updated bounding box (in this case, just {@code bbox}).
     */
    @Override
    public Rectangle updateBoundingBox(
        final Point startPos,
        final Rectangle bbox)
    {
        return bbox;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(18491, 15675)
            .appendSuper(super.hashCode())
            .toHashCode();
    }

    @Override
    public String toString() {
        return "MoveTo[" + this.getPoint().toString() + "]";
    }
}

