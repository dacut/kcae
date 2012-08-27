package kanga.kcae.object;

import static java.lang.Math.pow;
import static java.lang.Math.round;

import org.apache.commons.lang3.builder.HashCodeBuilder;

/** Draws a quadratic curve segment of a path.
 * 
 *  <p>Quadratic curves are parametric curves defined by two endpoints and a
 *  control point according to the formula:<br>
 *  Q(<i>t</i>) = (1-<i>t</i>)<sup>2</sup> P<sub>0</sub> +
 *  2 (1 - <i>t</i>) <i>t</i> P<sub>1</sub> +
 *  <i>t</i><sup>2</sup> P<sub>2</sub><br>
 *  for <i>t</i> in [0, 1].</p>
 */
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
    public Rectangle updateBoundingBox(
        final Point p0,
        Rectangle bbox)
    {
        final Point p1 = this.getControlPoint();
        final Point p2 = this.getTargetPoint();
        final Rectangle endpointRect = Rectangle.fromPoints(p0, p2);
        final long x0 = p0.getX();
        final long y0 = p0.getY();
        final long x1 = p1.getX();
        final long y1 = p1.getY();
        final long x2 = p2.getX();
        final long y2 = p2.getY();
        
        // Compute the extreme x and y positions.  The endpoints are always
        // possible extrema; we find extrema by setting the first derivative
        // of the parametric equations to zero.
        //
        // x = (1-t)^2 x0 + 2(1-t)t x1 + t^2 x2
        //   = x0 - 2 x0 t + x0 t^2 + 2 x1 t + 2 x1 t^2 + 2 x2 t^2
        //   = (x0 - 2 x1 + x2) t^2 + (-2 x0 + 2 x1) t + x0
        // x' = 2 (x0 - 2x1 + x2) t + (-2 x0 + 2 x1) == 0
        //      t@[x'==0] = (2 x0 - 2 x1) / (2 x0 - 4 x1 + 2 x2)
        //
        // And similarly for y.

        // First, just add the endpoints.
        if (bbox == null) {
            bbox = endpointRect;
        } else {
            bbox = bbox.union(endpointRect);
        }
        
        // Calculate the denominators of x' and y'.  These can be zero, which
        // means there are no closed form solutions along the given axis and
        // the endpoints are the extrema.
        final long xd = 2 * x0 - 4 * x1 + 2 * x2;
        final long yd = 2 * y0 - 4 * y1 + 2 * y2;
        
        if (xd != 0) {
            // Now the numerator for x.
            final long xn = 2 * x0 - 2 * x1;
            
            // Find t.
            final double tx = ((double) xn) / ((double) xd);
            
            // If the solution is sensical, 0 < t < 1
            if (tx > 0.0 && tx < 1.0) {
                final long x = round(pow(1 - tx, 2) * x0 +
                                     2 * (1 - tx) * tx * x1 +
                                     pow(tx, 2) * x2);
                
                // Just couple this with an endpoint.
                bbox = bbox.union(new Point(x, y0));
            }
        }
        
        if (yd != 0) {
            // Now the numerator for x.
            final long yn = 2 * y0 - 2 * y1;
            
            // Find t.
            final double ty = ((double) yn) / ((double) yd);
            
            // If the solution is sensical, 0 < t < 1
            if (ty > 0.0 && ty < 1.0) {
                final long y = round(pow(1 - ty, 2) * y0 +
                                     2 * (1 - ty) * ty * y1 +
                                     pow(ty, 2) * y2);
                
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
    private static final long serialVersionUID = 1L;
}

