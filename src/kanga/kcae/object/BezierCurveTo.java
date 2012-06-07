package kanga.kcae.object;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class BezierCurveTo implements PathInstruction {
    public BezierCurveTo(
        final Point controlPoint1,
        final Point controlPoint2,
        final Point targetPoint)
    {
        if (controlPoint1 == null) {
            throw new NullPointerException("controlPoint1 cannot be null.");
        }

        if (controlPoint2 == null) {
            throw new NullPointerException("controlPoint2 cannot be null.");
        }

        if (targetPoint == null) {
            throw new NullPointerException("targetPoint cannot be null.");
        }
                
        this.controlPoint1 = controlPoint1;
        this.controlPoint2 = controlPoint2;
        this.targetPoint = targetPoint;
    }

    public Point getControlPoint1() { return this.controlPoint1; }
    public Point getControlPoint2() { return this.controlPoint2; }
    public Point getTargetPoint() { return this.targetPoint; }

    @Override
    public void paint(PathPainter pp) {
        pp.bezierCurveTo(this.getControlPoint1(),
                         this.getControlPoint2(),
                         this.getTargetPoint());
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }
        
        final BezierCurveTo other = (BezierCurveTo) otherObj;
        return this.getControlPoint1().equals(other.getControlPoint1()) &&
            this.getControlPoint2().equals(other.getControlPoint2()) &&
            this.getTargetPoint().equals(other.getTargetPoint());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(24581, 7399)
            .append(this.getControlPoint1())
            .append(this.getControlPoint2())
            .append(this.getTargetPoint())
            .toHashCode();
    }

    @Override
    public String toString() {
        return "BezierCurveTo[" +
            "control1=" + this.getControlPoint1().toString() +
            ", control2=" + this.getControlPoint2().toString() +
            ", target=" + this.getTargetPoint().toString() + "]";
    }

    private final Point controlPoint1;
    private final Point controlPoint2;
    private final Point targetPoint;
}

