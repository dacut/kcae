package kanga.kcae.object;

import static java.lang.Math.pow;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/** Draws a cubic Bézier curve segment of a path.
 * 
 *  <p>Cubic Bézier curves are parametric curves defined by two endpoints
 *  P<sub>0</sub>, P<sub>3</sub> and two control points P<sub>1</sub>,
 *  P<sub>2</sub> according to the formula:<br>
 *  Q(<i>t</i>) = (1 - <i>t</i>)<sup>3</sup> P<sub>0</sub> +
 *  3 (1 - <i>t</i>)<sup>2</sup> <i>t</i> P<sub>1</sub> +
 *  3 (1 - <i>t</i>) <i>t</i><sup>2</sup> P<sub>2</sub> +
 *  t<sup>3</sup> P<sub>3</sub><br>
 *  for <i>t</i> in [0, 1].</p>
 */
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
    public Rectangle updateBoundingBox(final Point p0, Rectangle bbox) {
        final Point p1 = this.getControlPoint1();
        final Point p2 = this.getControlPoint2();
        final Point p3 = this.getTargetPoint();
        final Rectangle endpointRect = Rectangle.fromPoints(p0, p3);
        final long x0 = p0.getX();
        final long y0 = p0.getY();
        final long x1 = p1.getX();
        final long y1 = p1.getY();
        final long x2 = p2.getX();
        final long y2 = p2.getY();
        final long x3 = p3.getX();
        final long y3 = p3.getY();
        
        // Compute the extreme x and y positions.  The endpoints are always
        // possible extrema; we find extrema by setting the first derivative
        // of the parametric equations to zero.
        //
        // This yields two possible solutions for t:
        // t = (-6 p0 + 12 p1 - 6 p2 ±
        //      √((6 p0 - 12 p1 + 6 p2)^2 -
        //           4 (3 p1 - 3 p0) (-3 p0 + 9 p1 - 9 p2 + 3 p3))) /
        //     (2 (-3 p0 + 9 p1 - 9 p2 + 3 p3))
        // With p0 + 3 p2 ≠ 3 p1 + p3
        
        // First, just add the endpoints.
        if (bbox == null) {
            bbox = endpointRect;
        } else {
            bbox = bbox.union(endpointRect);
        }
        
        // Calculate the denominators of x' and y'.  These can be zero, which
        // means there are no closed form solutions along the given axis and
        // the endpoints are the extrema.
        // Calculate the denominators of x' and y'.  These can be zero, which
        // means there are no closed form solutions along the given axis and
        // the endpoints are the extrema.
        final long xd = 2 * (-3 * x0 + 9 * x1 - 9 * x2 + 3 * x3);
        final long yd = 2 * (-3 * y0 + 9 * y1 - 9 * y2 + 3 * y3);
        
        if (xd != 0) {
            // Find the discriminant (under the square root)...
            final double xdisc = 
                sqrt(pow(6 * x0 - 12 * x1 + 6 * x2, 2) -
                     4 * (3 * x1 - 3 * x0) *
                         (-3 * x0 + 9 * x1 - 9 * x2 + 3 * x3));
            // ... and the base
            final double xbase = -6.0 * x0 + 12.0 * x1 - 6.0 * x2;
            
            final double tx1 = (xbase - xdisc) / xd;
            final double tx2 = (xbase + xdisc) / xd;
            
            // If the solution is sensical, 0 < t < 1
            if (tx1 > 0.0 && tx1 < 1.0) {
                final long x = round(pow(1 - tx1, 3) * x0 +
                                     3 * pow(1 - tx1, 2) * tx1 * x1 +
                                     3 * (1 - tx1) * pow(tx1, 2) * x2 +
                                     pow(tx1, 3) * x3);
                
                // Just couple this with an endpoint.
                bbox = bbox.union(new Point(x, y0));
            }

            if (tx2 > 0.0 && tx2 < 1.0) {
                final long x = round(pow(1 - tx2, 3) * x0 +
                                     3 * pow(1 - tx2, 2) * tx2 * x1 +
                                     3 * (1 - tx2) * pow(tx2, 2) * x2 +
                                     pow(tx2, 3) * x3);

                // Just couple this with an endpoint.
                bbox = bbox.union(new Point(x, y0));
            }
        }
        
        if (yd != 0) {
            // Find the discriminant (under the square root)...
            final double ydisc = 
                    sqrt(pow(6 * y0 - 12 * y1 + 6 * y2, 2) -
                         4 * (3 * y1 - 3 * y0) *
                             (-3 * y0 + 9 * y1 - 9 * y2 + 3 * y3));
                // ... and the base
                final double ybase = -6.0 * y0 + 12.0 * y1 - 6.0 * y2;
                
                final double ty1 = (ybase - ydisc) / yd;
                final double ty2 = (ybase + ydisc) / yd;
                
                // If the solution is sensical, 0 < t < 1
                if (ty1 > 0.0 && ty1 < 1.0) {
                    final long y = round(pow(1 - ty1, 3) * y0 +
                                         3 * pow(1 - ty1, 2) * ty1 * y1 +
                                         3 * (1 - ty1) * pow(ty1, 2) * y2 +
                                         pow(ty1, 3) * y3);
                    
                    // Just couple this with an endpoint.
                    bbox = bbox.union(new Point(x0, y));
                }

                if (ty2 > 0.0 && ty2 < 1.0) {
                    final long y = round(pow(1 - ty2, 3) * y0 +
                                         3 * pow(1 - ty2, 2) * ty2 * y1 +
                                         3 * (1 - ty2) * pow(ty2, 2) * y2 +
                                         pow(ty2, 3) * y3);
                    
                    // Just couple this with an endpoint.
                    bbox = bbox.union(new Point(x0, y));
                }
        }        
        
        return bbox;
    }
    
    @Override
    public Point updatePosition(final Point startPos) {
        return this.getTargetPoint();
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

