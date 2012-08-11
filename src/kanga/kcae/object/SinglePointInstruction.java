package kanga.kcae.object;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class SinglePointInstruction implements PathInstruction {
    protected SinglePointInstruction(final Point point) {
        if (point == null) {
            throw new NullPointerException("point cannot be null.");
        }
                
        this.point = point;
    }

    public Point getPoint() { return this.point; }

    @Override
    public Rectangle updateBoundingBox(Point startPos, Rectangle bbox) {
        if (bbox == null) {
            bbox = Rectangle.fromPoints(startPos, this.getPoint());
        } else {
            bbox = bbox.union(startPos);
            bbox = bbox.union(this.getPoint());
        }
        
        return bbox;
    }
    
    @Override
    public Point updatePosition(final Point startPos) {
        return this.getPoint();
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }
        
        final MoveTo other = (MoveTo) otherObj;
        return this.getPoint().equals(other.getPoint());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(59015, 19355)
            .append(this.getPoint())
            .toHashCode();
    }

    private final Point point;
}

