package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;
import static java.lang.Math.asin;
import static java.lang.Math.atan2;
import static java.lang.Math.PI;
import static java.lang.Math.round;
import static java.lang.Math.toDegrees;

public class DrawArc extends ShapeInstruction {
    public DrawArc(
        int radius,
        boolean counterClockwise,
        double startAngleDegrees,
        double endAngleDegrees,
        boolean verticalOnly)
    {
        super(verticalOnly);
        this.radius = radius;
        this.counterClockwise = counterClockwise;
        this.startAngleDegrees = startAngleDegrees;
        this.endAngleDegrees = endAngleDegrees;
    }
    
    public int getRadius() {
        return this.radius;
    }

    public boolean isCounterClockwise() {
        return this.counterClockwise;
    }

    public double getStartAngleDegrees() {
        return this.startAngleDegrees;
    }

    public double getEndAngleDegrees() {
        return this.endAngleDegrees;
    }
    
    public static class OctantArcParser
        extends ShapeInstructionParser<DrawArc>
    {
        @Override
        public DrawArc parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            int op1 = is.read(); // radius
            int op2 = is.read(); // start/end octant, direction
            boolean ccw;
            int radius = op1;
            double startAngleDegrees, endAngleDegrees;
            
            if (op1 == -1 || op2 == -1) {
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }
            
            if (op2 >= 128) {
                ccw = false;
                op2 = 256 - op2;
            } else {
                ccw = true;
            }

            startAngleDegrees = 45.0 * ((op2 & 0x70) >> 4);
            endAngleDegrees =   45.0 *  (op2 & 0x07);
                
            return new DrawArc(
                radius, ccw, startAngleDegrees,
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
            int op1 = is.read(); // startOffset
            int op2 = is.read(); // endOffset
            int op3 = is.read(); // highRadius
            int op4 = is.read(); // lowRadius
            int op5 = is.read(); // start/end octant and direction
            boolean ccw;
            int radius;
            double startAngleDegrees, endAngleDegrees;
        
            if (op1 == -1 || op2 == -1 || op3 == -1 || op4 == -1 || op5 == -1) {
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
            
            return new DrawArc(
                radius, ccw, startAngleDegrees,
                endAngleDegrees, verticalOnly);
        }
    
        private static final long serialVersionUID = 1L;
    }

    private static final double HALF_PI = 0.5 * PI;
    
    public static class BulgeArcParser
        extends ShapeInstructionParser<DrawArc>
    {
        @Override
        public DrawArc parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            int dx = is.read();
            int dy = is.read();
            int b = is.read();

            if (dx == -1 || dy == -1 || b == -1) {
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }
            
            return generateBulgeArc(dx, dy, b, verticalOnly);
        }

        public DrawArc generateBulgeArc(
            int dx,
            int dy,
            int b,
            boolean verticalOnly)
        {
            boolean ccw;
            if (b >= 128) {
                ccw = false;
                b = 256 - b;
            } else {
                ccw = true;
            }

            // See the "AutoCAD Bulge Arc Analysis" Mathematica notebook for
            // details on the following computation.
            int d2 = dx * dx + dy * dy;
            double r = ((double) 4 * b * b + d2) / (double)(8 * b);
            double beta = asin(((double)(r - b)) / (double) r);
            double delta = HALF_PI - beta;
            double alpha = atan2(dy, dx) - beta;
            double endAngle = HALF_PI - alpha;
            double startAngle = endAngle + 2.0 * delta;

            return new DrawArc(
                (int) round(r), ccw, toDegrees(startAngle), toDegrees(endAngle),
                verticalOnly);
        }

        private static final long serialVersionUID = 1L;
    }
    
    public static class ArrayBulgeArcParser extends BulgeArcParser {
        @Override
        public DrawArc parse(
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
            
            return generateBulgeArc(dx, dy, b, verticalOnly);
        }
        
        private static final long serialVersionUID = 1L;
    }

    private final int radius;
    private final boolean counterClockwise;
    private final double startAngleDegrees;
    private final double endAngleDegrees;
    private static final long serialVersionUID = 1L;
}
