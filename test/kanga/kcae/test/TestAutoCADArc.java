package kanga.kcae.test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;
import org.junit.Test;

import kanga.kcae.object.Glyph;
import kanga.kcae.object.ArcTo;
import kanga.kcae.object.MoveTo;
import kanga.kcae.object.Path;
import kanga.kcae.object.PathInstruction;
import kanga.kcae.object.Point;
import kanga.kcae.xchg.autocad.DrawArc;
import kanga.kcae.xchg.autocad.RelativeMoveTo;
import kanga.kcae.xchg.autocad.ShapeInstruction;
import kanga.kcae.xchg.autocad.ShapeToGlyph;

public class TestAutoCADArc extends TestCase {
    public static void checkArc(
        ShapeInstruction si,
        double startAngle,
        double endAngle,
        int radius)
    {
        DrawArc arc = DrawArc.class.cast(si);
        assertEquals(arc.getStartAngleDegrees(), startAngle, 1e-6);
        assertEquals(arc.getEndAngleDegrees(), endAngle, 1e-6);
        assertEquals(arc.getRadius(), radius);
    }
    
    public static void checkMoveTo(
        ShapeInstruction si,
        int dx,
        int dy)
    {
        RelativeMoveTo rmt = RelativeMoveTo.class.cast(si);
        assertEquals(rmt.getX(), dx);
        assertEquals(rmt.getY(), dy);
    }
    
    @Test
    public void testBulgeArc() throws Exception {
        byte[] data = new byte[] {
            // dx, dy, bulge
            40, 20, 50,
            10, 10, 0,
        };
        
        InputStream is = new ByteArrayInputStream(data);
        DrawArc.BulgeArcParser parser = new DrawArc.BulgeArcParser();
        
        checkArc(parser.parse(is, false, 1), 
            -106.41414642012002,
            -20.455751225724005,
            33);
        checkMoveTo(parser.parse(is, false, 1), 10, 10);
    }
    
    @Test
    public void testGlyph() throws Exception {
        DrawArc da = new DrawArc(20, 10, 30, false);
        ShapeToGlyph stg = new ShapeToGlyph(null);
        
        stg.handle(da);
        Glyph g = stg.getGlyph();
        Path p = g.getPath();
        
        List<PathInstruction> inst = p.getInstructions();
        assertEquals(inst.size(), 2);
        PathInstruction i0 = inst.get(0);
        PathInstruction i1 = inst.get(1);
        
        assertTrue(i0 instanceof MoveTo);
        assertTrue(i1 instanceof ArcTo);
        
        ArcTo at1 = ArcTo.class.cast(i1);
        Point center = at1.getCenter();
        assertEquals(center.getX(), -20);
        assertEquals(center.getY(), -3);
        assertEquals(at1.getIncludedAngleRadians(), 0.34906585039886592, 1e-6);
    }
}
