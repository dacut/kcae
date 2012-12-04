package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;
import static java.lang.Math.cos;
import static java.lang.Math.PI;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;
import static java.lang.Math.toDegrees;

/** AutoCAD instruction to draw an arc in the current path.
 *  
 *  <p>Unlike KCAE arcs, AutoCAD arcs are more naturally represented by their
 *  start and end angles (in degrees instead of radians) and length.  This
 *  handles octant arcs (where the arc must start on a multiple of 45º) and
 *  fractional arcs (where the arc must start on a multiple of 45/256º)
 *  nicely.</p>
 *  
 *  <p>The one exception are bulge-specified arcs.  These arcs are encoded by
 *  their endpoint, bulge factor, and direction.  It would be cumbersome to
 *  have a special representation for them in memory, so we just perform the
 *  mathematical gymnastics to convert them to our standard representation
 *  during the parse phase.</p>
 */
public class DrawArc extends ShapeInstruction {
    /** Create a new DrawArc instruction.
     * 
     *  <p>Unlike AutoCAD, we do not store the direction separately.  Arcs are
     *  always drawn from their start point to their end point.  Thus, if
     *  {@code startAngleDegrees < endAngleDegrees}, the arc is drawn
     *  counterclockwise; otherwise, it is drawn clockwise.</p>
     *  
     *  <p>This class is immutable.</p>
     * 
     *  @param radius   The radius of the arc in arbitrary AutoCAD units.
     *  @param startAngleDegrees The start angle of the arc in degrees.
     *  @param endAngleDegrees The end angle of the arc.
     *  @param verticalOnly If true, this instruction applies only when the
     *         shape is being drawn in a vertical orientation.
     */
    public DrawArc(
        final int radius,
        final double startAngleDegrees,
        final double endAngleDegrees,
        final boolean verticalOnly)
    {
        super(verticalOnly);
        this.radius = radius;
        this.startAngleDegrees = startAngleDegrees;
        this.endAngleDegrees = endAngleDegrees;
    }

    @Override
    public void visit(ShapeInstructionHandler handler) {
        handler.handle(this);
    }
    
    /** Returns the radius of the arc.
     * 
     *  @return The radius of the arc.
     */
    public int getRadius() {
        return this.radius;
    }
    
    /** The starting angle of the arc in degrees.
     * 
     *  @return The starting angle of the arc in degrees.
     */
    public double getStartAngleDegrees() {
        return this.startAngleDegrees;
    }
    
    /** The ending angle of the arc in degrees.
     * 
     *  @return The ending angle of the arc in degrees.
     */
    public double getEndAngleDegrees() {
        return this.endAngleDegrees;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (! super.equals(otherObj)) { return false; }
        
        DrawArc other = DrawArc.class.cast(otherObj);
        return new EqualsBuilder()
            .append(this.getRadius(), other.getRadius())
            .append(this.getStartAngleDegrees(), other.getStartAngleDegrees())
            .append(this.getEndAngleDegrees(), other.getEndAngleDegrees())
            .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(434086357, 1262819003)
            .appendSuper(super.hashCode())
            .append(this.getRadius())
            .append(this.getStartAngleDegrees())
            .append(this.getEndAngleDegrees())
            .toHashCode();
    }
    
    @Override
    protected ToStringBuilder toStringBuilder() {
        return super.toStringBuilder()
            .append("radius", this.getRadius())
            .append("startAngleDegrees", this.getStartAngleDegrees())
            .append("endAngleDegrees", this.getEndAngleDegrees());
    }
    
    /** Parses an octant arc directive from a SHX file.
     *  
     *  <p>Octant arcs are encoded in three bytes as follows:<br>
     *  <tt>0x0A <i>radius</i> <i>dsc</i></tt><br>
     *  The <i>dsc</i> byte is further broken down into the following bits:
     *  <table border="1" cellpadding="1" cellspacing="0">
     *  <thead>
     *    <tr>
     *      <th>7</th><th>6</th><th>5</th><th>4</th><th>3</th><th>2</th>
     *      <th>1</th><th>0</th></tr>
     *  </thead>
     *  <tbody>
     *    <tr>
     *      <td><i>dir</i></td>
     *      <td colspan="3"><i>startOctant</i></td>
     *      <td>0</td>
     *      <td colspan="3"><i>spanOctants</i></td>
     *    </tr>
     *  </tbody>
     *  </table></p>
     */
    public static class OctantArcParser
        extends ShapeInstructionParser<DrawArc>
    {
        /** Parse an octant arc directive from a SHX file.
         * 
         *  This assumes the octant arc instruction has already been read
         *  from the stream.
         * 
         *  @param is   The stream to read the bytes from.
         *  @param verticalOnly Whether a vertical-only prefix preceded this
         *              instruction.
         *  @param shapeId The id of the shape this directive is a part of.
         */
        @Override
        public DrawArc parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            final Log log = LogFactory.getLog(
                OctantArcParser.class.getName() + "." +
                String.format("%04x", shapeId));
            int op1 = is.read(); // radius
            int op2 = is.read(); // start/end octant, direction
            boolean ccw;
            int radius = op1;
            int startOctant, nOctants;
            double startAngleDegrees, endAngleDegrees;
            
            if (op1 == -1 || op2 == -1) {
                log.error("Truncated octantarc; op1=" + op1 + " op2=" + op2);
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }
            
            // This appears to be a bug in the AutoCAD spec.  The high bit is
            // *not* used as a sign bit here, despite what AutoCAD claims.
            // It's actually a clockwise bit; the other bits are *not*
            // in 2's complement format.
            ccw = ((op2 & 0x80) == 0);
            op2 = op2 & 0x7f;
            
            startOctant = ((op2 & 0x70) >> 4);
            
            // If nOctants == 0, this actually indicates a full circle
            // (i.e. should be 8).
            nOctants = op2 & 0x07;
            if (nOctants == 0) {
                nOctants = 8;
            }

            startAngleDegrees = 45 * startOctant;
            endAngleDegrees = startAngleDegrees + 
                45 * (ccw ? 1 : -1) * nOctants;
            
            log.debug("op1=" + op1 + " op2=" + op2 + " ccw=" + ccw +
                      " startOctant=" + startOctant + " nOctants=" + nOctants +
                      " startAngle=" + startAngleDegrees + "º endAngle=" +
                      endAngleDegrees + "º");
            
            // Normalize to be in the range -360 to +360.
            if (ccw && endAngleDegrees > 360.0) {
                endAngleDegrees -= 360.0;
                startAngleDegrees -= 360.0;
            }
                
            return new DrawArc(
                radius, startAngleDegrees,
                endAngleDegrees, verticalOnly);
        }
        
        private static final long serialVersionUID = 1L;
    }

    public static class FractionalArcParser
        extends ShapeInstructionParser<DrawArc>
    {
        @Override
        public DrawArc parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            final Log log = LogFactory.getLog(
                FractionalArcParser.class.getName() + "." +
                String.format("%04x", shapeId));
            int op1 = is.read(); // startOffset
            int op2 = is.read(); // endOffset
            int op3 = is.read(); // highRadius
            int op4 = is.read(); // lowRadius
            int op5 = is.read(); // start/end octant and direction
            boolean ccw;
            int radius;
            double startAngleDegrees, endAngleDegrees;
        
            if (op1 == -1 || op2 == -1 || op3 == -1 || op4 == -1 || op5 == -1) {
                log.error("Truncated fractionalarc; op1=" + op1 + " op2=" +
                          op2 + " op3=" + op3 + " op4=" + op4 + " op5=" + op5);
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }
        
            if (op5 >= 128) {
                ccw = false;
                op5 = 256 - op5;
            } else {
                ccw = true;
            }
            
            startAngleDegrees = 45.0 * (
                ((op5 & 0x70) >> 4) + op1 / 256.0);
            endAngleDegrees =   45.0 * (
                (op2 & 0x07) + op2 / 256.0);
            radius = op3 * 256 + op4;

            log.debug("op1=" + op1 + " op2=" + op2 + " op3=" + op3 + " op4=" +
                      op4 + " op5=" + op5 + " ccw=" + ccw + " startAngle=" + 
                      startAngleDegrees + "º endAngle=" + endAngleDegrees +
                      "º");
            
            if (ccw) {
                // startAngle must be less than endAngle
                while (startAngleDegrees > endAngleDegrees) {
                    startAngleDegrees -= 360.0;
                }
            } else {
                // startAngle must be greater than endAngle
                while (startAngleDegrees < endAngleDegrees) {
                    startAngleDegrees += 360.0;
                }            
            }            
            
            return new DrawArc(
                radius, startAngleDegrees, endAngleDegrees, verticalOnly);
        }
    
        private static final long serialVersionUID = 1L;
    }

    private static final double HALF_PI = 0.5 * PI;
    
    public static class BulgeArcParser
        extends ShapeInstructionParser<ShapeInstruction>
    {
        @Override
        public ShapeInstruction parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            final Log log = LogFactory.getLog(
                BulgeArcParser.class.getName() +
                "." + String.format("%04x", shapeId));
            
            int dx = is.read();
            int dy = is.read();
            int b = is.read();

            if (dx == -1 || dy == -1 || b == -1) {
                log.error("Truncated bulgearc; dx=" + dx + " dy=" + dy +
                          " b=" + b);
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }
            
            if (dx > 127)
                dx -= 256;
            
            if (dy > 127)
                dy -= 256;
            
            if (b > 127)
                b -= 256;
                
            if (b == 0) {
                // Degenerate arc -- this is actually a straight line.
                return new RelativeMoveTo(dx, dy, verticalOnly);
            }
            
            return generateBulgeArc(dx, dy, b, verticalOnly);
        }

        public DrawArc generateBulgeArc(
            final int dx,
            final int dy,
            final int bulgeFactor,
            final boolean verticalOnly)
        {
            final double qx = 0.5 * dx;
            final double qy = 0.5 * dy;
            final double d = sqrt(qx * qx + qy * qy);
            final double b = bulgeFactor * d / 127.0;
            final double r = (b * b + d * d) / (2.0 * b);
            final double omega = atan2(dy, dx) + HALF_PI;
            final double ox = qx + (r - b) * cos(omega);
            final double oy = qy + (r - b) * sin(omega);
            double startAngle = atan2(-oy, -ox);
            double endAngle = atan2(dy - oy, dx - ox);
            
            if (bulgeFactor > 0) {
                while (startAngle > endAngle)
                    startAngle -= 2.0 * PI;
            } else {
                while (startAngle < endAngle)
                    startAngle += 2.0 * PI;
            }
            
            return new DrawArc((int) abs(round(r)), toDegrees(startAngle),
                               toDegrees(endAngle), verticalOnly);
        }

        private static final long serialVersionUID = 1L;
    }
    
    public static class ArrayBulgeArcParser extends BulgeArcParser {
        @Override
        public ShapeInstruction parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            int dx = is.read();
            int dy = is.read();

            if (dx == 0 && dy == 0) {
                // Array terminated; note that no bulge specifier follows.
                throw arrayDone;
            }
            
            int b = is.read();
            
            if (dx == -1 || dy == -1 || b == -1) {
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }
            
            if (dx > 127)
                dx -= 256;
            
            if (dy > 127)
                dy -= 256;
            
            if (b > 127)
                b -= 256;
            
            if (b == 0) {
                return new RelativeMoveTo(dx, dy, verticalOnly);
            }
            
            return generateBulgeArc(dx, dy, b, verticalOnly);
        }
        
        @Override
        public boolean isArray() {
            return true;
        }
        
        private static final long serialVersionUID = 1L;
    }

    private final int radius;
    private final double startAngleDegrees;
    private final double endAngleDegrees;
    private static final long serialVersionUID = 1L;
}
