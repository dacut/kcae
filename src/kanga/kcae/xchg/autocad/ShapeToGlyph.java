package kanga.kcae.xchg.autocad;

import java.util.Deque;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import static java.lang.Math.cos;
import static java.lang.Math.round;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;

import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
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
    private static final LineStyle defaultLineStyle =
        new LineStyle(100000, Color.black);
    
    /** Create a new ShapeToGlyph translator to convert an AutoCAD shape into
     *  a KCAE shape.
     * 
     *  @param  shapes      A map of the other AutoCAD shapes in the shape file.
     *                      This is needed to draw subshapes embedded within a
     *                      shape.
     *  @param  lineStyle   The initial line style to use.
     *  @param  conversionScale The conversion scale to use for translating
     *                      AutoCAD's arbitrary coordinate system into
     *                      nanometers.
     */
    public ShapeToGlyph(final Map<Character, AutoCADShape> shapes,
                        @Nullable LineStyle lineStyle,
                        final double conversionScale)
    {
        this.shapes = shapes;
        this.lineStyle = defaultIfNull(lineStyle, defaultLineStyle);
        this.pointStack = new ArrayDeque<Point>();        
        this.conversionScale = conversionScale;
        this.drawing = true;
        this.log = staticLog;
    }
    
    public void startTranslation(char c) {
        this.log = LogFactory.getLog(
            ShapeToGlyph.class.getName() + "." +
            String.format("%04x", (int) c));
        this.path = new Path();
        this.path.setLineStyle(this.lineStyle);
        this.pointStack.clear();
        this.currentPoint = new Point(0, 0);
        this.scaleFactor = 1;
        this.path.addInstruction(new MoveTo(0, 0));
        
        this.log.debug("Translating " + c);
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
        final long radius = round(inst.getRadius() * this.conversionScale);
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
        
        this.log.debug("Converting " + inst + " to " + caeInst);
        
        this.path.addInstruction(caeInst);
        
    }

    @Override
    public void handle(DrawDirectionalLine inst) {
        if (inst.verticalOnly()) { return; }
        
        final DirectionCode dc = inst.getDirectionCode();
        final int length = inst.getLength() * this.scaleFactor;
        final long x = this.currentPoint.getX() +
            round(dc.relX * length * this.conversionScale);
        final long y = this.currentPoint.getY() +
            round(dc.relY * length * this.conversionScale);
        final PathInstruction caeInst;
        
        if (this.drawing) {
            caeInst = new LineTo(x, y);
        } else {
            caeInst = new MoveTo(x, y);
        }
        
        this.log.debug("Converting " + inst + " to " + caeInst);
        this.path.addInstruction(caeInst);
        
        this.currentPoint = new Point(x, y);
    }

    @Override
    public void handle(DrawSubshape inst) {
        if (inst.verticalOnly()) { return; }
        
        final char subshapeId = inst.getSubshapeId();
        final AutoCADShape subshape = this.shapes.get(subshapeId);

        this.log.debug("Converting subshape " + inst);
        
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
        
        this.log.debug("ScaleFactor changing from " + this.scaleFactor +
                       " to " + (this.scaleFactor * inst.getFactor()));
        this.scaleFactor *= inst.getFactor();
    }

    @Override
    public void handle(PopLocation inst) {
        if (inst.verticalOnly()) { return; }
        
        this.currentPoint = this.pointStack.pop();
        final MoveTo caeInst = new MoveTo(this.currentPoint);
        
        this.log.debug("Converting " + inst + " to " + caeInst);
        
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
            round(inst.getX() * this.conversionScale),
            round(inst.getY() * this.conversionScale));
        final PathInstruction caeInst;
        
        if (this.drawing) {
            caeInst = new LineTo(this.currentPoint);
        } else {
            caeInst = new MoveTo(this.currentPoint);
        }
        
        this.log.debug("Converting " + inst + " to " + caeInst);
        
        this.path.addInstruction(caeInst);
    }

    public Path getPath() {
        return this.path;
    }
    
    public Glyph getGlyph() {
        this.log = staticLog;
        return new Glyph(this.path, this.currentPoint.getX(), null);
    }
    
    public int getScaleFactor() {
        return this.scaleFactor;
    }
    
    public void setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
    }
    
    private static final Log staticLog = LogFactory.getLog(ShapeToGlyph.class);
    private Log log;
    private Path path;
    private LineStyle lineStyle;
    private final Map<Character, AutoCADShape> shapes;
    private final Deque<Point> pointStack;
    private Point currentPoint;
    private int scaleFactor;
    private double conversionScale;
    private boolean drawing;
}
