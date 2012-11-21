package kanga.kcae.object;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class SinglePointInstruction
    implements PathInstruction, Serializable
{
    protected SinglePointInstruction(final Point point) {
        if (point == null) {
            throw new NullPointerException("point cannot be null.");
        }
                
        this.point = point;
    }

    public Point getPoint() { return this.point; }

    @Override
    @Nonnull
    public Rectangle updateBoundingBox(
        @CheckForNull Point startPos,
        @CheckForNull Rectangle bbox)
    {
        Point endPos = this.getPoint();
        
        if (bbox == null) {
            if (startPos == null) {
                bbox = Rectangle.fromPoints(endPos, endPos);
            } else {
                bbox = Rectangle.fromPoints(startPos, endPos);
            }
        } else {
            bbox = bbox.union(startPos);
            bbox = bbox.union(endPos);
        }
        
        return bbox;
    }
    
    @Override
    @Nonnull
    public Point updatePosition(@CheckForNull Point startPos) {
        return this.getPoint();
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }
        
        final SinglePointInstruction other = (SinglePointInstruction) otherObj;
        return this.getPoint().equals(other.getPoint());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(59015, 19355)
            .append(this.getPoint())
            .toHashCode();
    }

    private final Point point;
    private static final long serialVersionUID = 1L;
}

