package kanga.kcae.xchg.autocad;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;

import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.ArcTo;
import kanga.kcae.object.Color;
import kanga.kcae.object.Glyph;
import kanga.kcae.object.LineStyle;
import kanga.kcae.object.LineTo;
import kanga.kcae.object.MoveTo;
import kanga.kcae.object.Path;
import kanga.kcae.object.PathInstruction;
import kanga.kcae.object.Point;

public class ShapeToGlyph implements ShapeInstructionHandler {
    private static final Log log = LogFactory.getLog(ShapeToGlyph.class);
    private static final LineStyle defaultLineStyle =
        new LineStyle(100000, Color.black);
        
    public ShapeToGlyph(final Map<Character, AutoCADShape> shapes) {
        this.path = new Path();
        this.path.setLineStyle(defaultLineStyle);
        this.shapes = shapes;
        this.pointStack = new ArrayDeque<Point>();
        this.currentPoint = new Point(0, 0);
        this.scaleFactor = 1;
        this.drawing = true;
        
        this.path.addInstruction(new MoveTo(0, 0));
    }
    
    
    
    @Override
    public void handle(ActivateDraw inst) {
        if (inst.verticalOnly()) { return; }
        this.drawing = true;
    }

    @Override
    public void handle(DeactivateDraw inst) {
        if (inst.verticalOnly()) { return; }
        this.drawing = false;
    }

    @Override
    public void handle(DivideVectorLength inst) {
        if (inst.verticalOnly()) { return; }
        
        this.scaleFactor /= inst.getFactor();
    }

    @Override
    public void handle(DrawArc inst) {
        if (inst.verticalOnly()) { return; }
        
        final double startAngle = toRadians(inst.getStartAngleDegrees());
        double endAngle = toRadians(inst.getEndAngleDegrees());
        
        final double includedAngle = endAngle - startAngle;
        final long radius = inst.getRadius() * this.scaleFactor;
        final Point startPoint = this.currentPoint;
        final Point center = new Point(
            startPoint.getX() - round(radius * cos(startAngle)),
            startPoint.getY() - round(radius * sin(startAngle)));
        final PathInstruction caeInst;

        this.currentPoint = new Point(
            center.getX() + round(radius * cos(endAngle)),
            center.getY() + round(radius * sin(endAngle)));
        
        if (this.drawing) {
            caeInst = new ArcTo(center, includedAngle);
        } else {
            caeInst = new MoveTo(this.currentPoint);
        }
        
        log.debug("Converting " + inst + " to " + caeInst);
        this.path.addInstruction(caeInst);
        
    }

    @Override
    public void handle(DrawDirectionalLine inst) {
        if (inst.verticalOnly()) { return; }
        
        final DirectionCode dc = inst.getDirectionCode();
        final int length = inst.getLength() * this.scaleFactor;
        final long x = this.currentPoint.getX() + round(dc.relX * length);
        final long y = this.currentPoint.getY() + round(dc.relY * length);
        final PathInstruction caeInst;
        
        if (this.drawing) {
            caeInst = new LineTo(x, y);
        } else {
            caeInst = new MoveTo(x, y);
        }
        
        log.debug("Converting " + inst + " to " + caeInst);
        this.path.addInstruction(caeInst);
        
        this.currentPoint = new Point(x, y);
    }

    @Override
    public void handle(DrawSubshape inst) {
        if (inst.verticalOnly()) { return; }
        
        final char subshapeId = inst.getSubshapeId();
        final AutoCADShape subshape = this.shapes.get(subshapeId);
        log.debug("Converting subshape " + inst);
        
        if (subshape != null) {
            List<ShapeInstruction> subinstrs = subshape.getInstructions();
            for (final ShapeInstruction si : subinstrs) {
                si.visit(this);
            }
        }
    }

    @Override
    public void handle(MultiplyVectorLength inst) {
        if (inst.verticalOnly()) { return; }
        
        log.debug("ScaleFactor changing from " + this.scaleFactor + " to " +
                  (this.scaleFactor * inst.getFactor()));
        this.scaleFactor *= inst.getFactor();
    }

    @Override
    public void handle(PopLocation inst) {
        if (inst.verticalOnly()) { return; }
        
        this.currentPoint = this.pointStack.pop();
        final MoveTo caeInst = new MoveTo(this.currentPoint);
        log.debug("Converting " + inst + " to " + caeInst);
        this.path.addInstruction(caeInst);
    }

    @Override
    public void handle(PushLocation inst) {
        if (inst.verticalOnly()) { return; }
        
        this.pointStack.push(this.currentPoint);
    }

    @Override
    public void handle(RelativeMoveTo inst) {
        if (inst.verticalOnly()) { return; }
        
        this.currentPoint = this.currentPoint.translate(
            inst.getX(), inst.getY());
        final PathInstruction caeInst;
        
        if (this.drawing) {
            caeInst = new LineTo(this.currentPoint);
        } else {
            caeInst = new MoveTo(this.currentPoint);
        }
        
        log.debug("Converting " + inst + " to " + caeInst);
        this.path.addInstruction(caeInst);
    }

    public Path getPath() {
        return this.path;
    }
    
    public Glyph getGlyph() {
        return new Glyph(this.path, this.currentPoint.getX(), null);
    }
    
    private final Path path;
    private final Map<Character, AutoCADShape> shapes;
    private final Deque<Point> pointStack;
    private Point currentPoint;
    private int scaleFactor;
    private boolean drawing;
}
