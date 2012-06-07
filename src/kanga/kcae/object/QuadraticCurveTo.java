package kanga.kcae.object;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class QuadraticCurveTo implements PathInstruction {
    public QuadraticCurveTo(final Point controlPoint, final Point targetPoint) {
        if (controlPoint == null) {
            throw new NullPointerException("controlPoint cannot be null.");
        }

        if (targetPoint == null) {
            throw new NullPointerException("targetPoint cannot be null.");
        }
                
        this.controlPoint = controlPoint;
        this.targetPoint = targetPoint;
    }

    public Point getControlPoint() { return this.controlPoint; }
    public Point getTargetPoint() { return this.targetPoint; }

    @Override
    public void paint(PathPainter pp) {
        pp.quadraticCurveTo(this.getControlPoint(), this.getTargetPoint());
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }
        
        final QuadraticCurveTo other = (QuadraticCurveTo) otherObj;
        return this.getControlPoint().equals(other.getControlPoint()) &&
            this.getTargetPoint().equals(other.getTargetPoint());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(37487, 58593)
            .append(this.getControlPoint())
            .append(this.getTargetPoint())
            .toHashCode();
    }

    @Override
    public String toString() {
        return "QuadraticCurveTo[control=" + this.getControlPoint().toString() +
            ", target=" + this.getTargetPoint().toString() + "]";
    }

    private final Point controlPoint;
    private final Point targetPoint;
}

