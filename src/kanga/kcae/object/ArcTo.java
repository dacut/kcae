package kanga.kcae.object;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.signum;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.PI;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ArcTo implements PathInstruction {
    @edu.umd.cs.findbugs.annotations.SuppressWarnings(
        {"MS_CANNOT_BE_FINAL", "MS_SHOULD_BE_FINAL"})
    public static boolean enableDebug = false;
    
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(ArcTo.class);
    public static final double PI_2 = 0.5 * PI;
    public static final double PI_4 = 0.25 * PI;
    
    public ArcTo(
        Point center,
        double includedAngleRadians)
    {
        this.center = center;
        this.includedAngleRadians = includedAngleRadians;
    }
    
    public Point getCenter() {
        return this.center;
    }
    
    public double getIncludedAngleRadians() {
        return this.includedAngleRadians;
    }
    
    public double getStartAngleRadians(final Point start) {
        return getStartAngleRadians(start, this.getCenter());
    }
    
    public double getEndAngleRadians(final Point start) {
        return getEndAngleRadians(
            start, this.getCenter(), this.getIncludedAngleRadians());
    }
    
    public Point getEndPoint(final Point start) {
        return getEndPoint(
            start, this.getCenter(), this.getIncludedAngleRadians());
    }
    
    @Override
    @Nonnull
    public ArcTo scale(double factor) {
        return new ArcTo(this.getCenter().scale(factor),
                         this.getIncludedAngleRadians());
    }

    @Override
    @Nonnull
    public ArcTo translate(long dx, long dy) {
        return new ArcTo(this.getCenter().translate(dx, dy),
                         this.getIncludedAngleRadians());
    }

    @Override
    @Nonnull
    public ArcTo rotateQuadrant(int nQuadrants) {
        return new ArcTo(this.getCenter().rotateQuadrant(nQuadrants),
                         this.getIncludedAngleRadians());
    }

    @Override
    public void paint(@Nonnull PathPainter pp) {
        pp.arcTo(this.getCenter(), getIncludedAngleRadians());
    }
    
    /** Converts an arc into one or more Bézier curves and paints the Bézier
     *  curves.
     *  
     *  <p>
     *  This is required for graphics systems which cannot paint arcs as
     *  part of a path.  This includes PostScript and Java2D.
     *  </p>
     * 
     *  @param pp       The painter to paint the arc onto.
     *  @param start The starting point of the arc.
     *  @param center   The center point of the arc.
     *  @param includedAngleRadians The size of the included angle of the arc,
     *                  in radians.
     *                  
     *  @return The end point of the arc.
     */
    public static Point paintAsBezier(
        @Nonnull final PathPainter pp,
        @Nonnull final Point start,
        @Nonnull final Point center,
        @Nonnull final double includedAngleRadians)
    {
        // Our conversion formula breaks down at π/2, and accuracy is improved
        // by using smaller included angles.  We limit the size of our angles
        // here to π/4.
        if (abs(includedAngleRadians) > PI_4) {
            // Paint only half of the arc.
            final double paintAngle = 0.5 * includedAngleRadians;
            final Point midPoint = paintAsBezier(pp, start, center, paintAngle);
            return paintAsBezier(pp, midPoint, center, paintAngle);
        }
        
        final double a = getStartAngleRadians(start, center);
        final double b = getEndAngleRadians(
            start, center, includedAngleRadians);
        final Point end = getEndPoint(start, center, includedAngleRadians);
        final double r = getRadius(start, center);

        final double dir = signum(includedAngleRadians);
        final double k = 
            (-4.0 * (start.getX() - center.getX() - 
                   r * (2 * cos(includedAngleRadians * 0.5 + a) - cos(b))))
            /
            ( 3.0 * (cos(b - PI_2 * dir) + 
                     cos(a + PI_2 * dir)));

        final Point cp1 = start.translate(round(k * cos(a + dir * PI_2)),
                                          round(k * sin(a + dir * PI_2)));
        final Point cp2 = end.translate(round(k * cos(b - dir * PI_2)),
                                        round(k * sin(b - dir * PI_2)));
        
        pp.bezierCurveTo(cp1, cp2, end);
        
        return end;
    }

    @Override
    @Nonnull
    public Rectangle updateBoundingBox(
        @Nullable @CheckForNull Point startPos,
        @Nullable @CheckForNull Rectangle bbox)
    {
        // FIXME: This calculation is incorrect.
        
        if (startPos == null) {
            return Rectangle.fromPoints(0, 0, 0, 0);
        }
        
        final Point endPos = this.updatePosition(startPos);
        final Rectangle myBBox = Rectangle.fromPoints(startPos, endPos);
        
        if (enableDebug)
            log.debug("updateBoundingBox: startPos=" + startPos + " endPos=" + endPos + " bbox=" + bbox + " myBBox=" + myBBox);
        
        if (bbox == null) {
            return myBBox;
        } else {
            return bbox.union(myBBox);
        }
    }

    @Override
    @Nullable
    public Point updatePosition(@Nullable @CheckForNull Point start) {
        if (start == null)
            return null;
        
        final double angle = this.getIncludedAngleRadians();
        final Point center = this.getCenter();
        final long cx = center.getX();
        final long cy = center.getY();
        final long sx = start.getX();
        final long sy = start.getY();
        final long dx = cx - sx;
        final long dy = cy - sy;
        final double startAngle = atan2(dy, dx);
        final double endAngle = startAngle + angle;
        final double r = sqrt(dx * dx + dy * dy);
        final long ex = cx + round(r * cos(endAngle));
        final long ey = cy + round(r * sin(endAngle));
        
        return new Point(ex, ey);
    }
    
    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (this == otherObj) { return true; }
        if (this.getClass() != otherObj.getClass()) { return false; }
        
        final ArcTo other = ArcTo.class.cast(otherObj);
        return new EqualsBuilder()
            .append(this.getCenter(), other.getCenter())
            .append(this.getIncludedAngleRadians(),
                other.getIncludedAngleRadians())
            .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(331, 673)
            .append(this.getCenter())
            .append(this.getIncludedAngleRadians())
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("center", this.getCenter())
            .append("includedAngle", this.getIncludedAngleRadians())
            .toString();
    }
    
    public static double getRadius(
        final Point start,
        final Point center)
    {
        double dx = start.getX() - center.getX();
        double dy = start.getY() - center.getY();
        return sqrt(dx * dx + dy * dy);
    }

    public static double getStartAngleRadians(
        final Point start,
        final Point center)
    {
        return atan2(
            start.getY() - center.getY(),
            start.getX() - center.getX());
    }
    
    public static double getEndAngleRadians(
        final Point start,
        final Point center,
        final double includedAngleRadians)
    {
        return includedAngleRadians + getStartAngleRadians(start, center);
    }
    
    public static Point getEndPoint(
        final Point start,
        final Point center,
        final double includedAngleRadians)
    {
        final double endAngle = getEndAngleRadians(
            start, center, includedAngleRadians);
        final double radius = center.getDistance(start);
        
        return new Point(
            center.getX() + round(radius * cos(endAngle)),
            center.getY() + round(radius * sin(endAngle)));
    }

    private final Point center;
    private final double includedAngleRadians;

    private static final long serialVersionUID = 1L;
}
