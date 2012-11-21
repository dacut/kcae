package kanga.kcae.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import junit.framework.TestCase;
import kanga.kcae.object.Glyph;
import kanga.kcae.object.Typeface;
import kanga.kcae.xchg.autocad.ActivateDraw;
import kanga.kcae.xchg.autocad.AutoCADShape;
import kanga.kcae.xchg.autocad.AutoCADShapeFile;
import kanga.kcae.xchg.autocad.DeactivateDraw;
import kanga.kcae.xchg.autocad.DrawArc;
import kanga.kcae.xchg.autocad.RelativeMoveTo;
import kanga.kcae.xchg.autocad.ShapeInstruction;

import org.junit.Test;

public class TestAutoCADShapeFile extends TestCase {
    static AutoCADShapeFile getISOFont() throws IOException {
        InputStream iso3098b = ClassLoader.getSystemResourceAsStream(
            "kanga/kcae/res/iso3098b.shx");
        assertNotNull(iso3098b);
        return AutoCADShapeFile.fromCompiledFile(iso3098b);
    }
    
    @Test
    public void testRead() throws Exception {
        AutoCADShapeFile sf = getISOFont();
        assertTrue(sf.isFont());
    }
    
    @Test
    public void testAmpersand() throws Exception {
        AutoCADShapeFile sf = getISOFont();
        AutoCADShape amp = sf.getShapes().get('&');
        
        assertNotNull(amp);
        
        List<ShapeInstruction> ampInst = amp.getInstructions();
        
        assertNotNull(ampInst);
        
        // Ampersand bytecodes: (* do not generate instructions)
        //      2,                    0 DeactivateDraw
        //      14,8,(-7,-20),        1 VOnly RelativeMoveTo(-7,-20)
        //      8,(11,1),             2 RelativeMoveTo(11,1) 
        //      1,                    3 ActivateDraw
        //      8,(-7,11),            4 RelativeMoveTo(-7,11)
        //      13,                   * BulgeArcArray
        //         (-1,4,-21),        5 DrawArc
        //         (3,3,-50),         6 DrawArc
        //         (3,-2,-44),        7 DrawArc
        //         (-1,-4,-37),       8 DrawArc
        //         (-6,-6,6),         9 DrawArc
        //         (-1,-3,24),       10 DrawArc
        //         (1,-2,27),        11 DrawArc
        //         (5,0,46),         12 DrawArc
        //         (6,5,0),          13 DrawArc
        //         (0,0),             * EndOfArray
        //      2,                   14 DeactivateDraw
        //      8,(5,-7),            15 RelativeMoveTo(5,-7)
        //      14,8,(-11,-10),      16 VOnly RelativeMoveTo(-11, -10)
        //      0                     * End

        assertEquals(17, ampInst.size());
        Class<?>[] instClasses = new Class[] {
            DeactivateDraw.class, RelativeMoveTo.class, RelativeMoveTo.class,
            ActivateDraw.class, RelativeMoveTo.class, DrawArc.class,
            DrawArc.class, DrawArc.class, DrawArc.class, DrawArc.class,
            DrawArc.class, DrawArc.class, DrawArc.class, DrawArc.class,
            DeactivateDraw.class, RelativeMoveTo.class, RelativeMoveTo.class
        };
        
        for (int i = 0; i < instClasses.length; ++i) {
            assertSame(
                "Instruction " + i + " should be a " +
                instClasses[i].getSimpleName() + " object; got " +
                ampInst.get(i).getClass().getSimpleName(),
                instClasses[i], ampInst.get(i).getClass());
        }
        
        RelativeMoveTo rmt;
        DrawArc da;
        
        rmt = RelativeMoveTo.class.cast(ampInst.get(1));
        assertEquals(-7, rmt.getX());
        assertEquals(-20, rmt.getY());
        assertTrue(rmt.verticalOnly());
        
        rmt = RelativeMoveTo.class.cast(ampInst.get(2));
        assertEquals(11, rmt.getX());
        assertEquals(1, rmt.getY());
        assertFalse(rmt.verticalOnly());
        
        rmt = RelativeMoveTo.class.cast(ampInst.get(4));
        assertEquals(-7, rmt.getX());
        assertEquals(11, rmt.getY());
        assertFalse(rmt.verticalOnly());
        
        da = DrawArc.class.cast(ampInst.get(5));
        assertEquals(6, da.getRadius());
        assertEquals(212.8145385905890, da.getStartAngleDegrees(), 1e-9);
        assertEquals(175.2579483452640, da.getEndAngleDegrees(), 1e-9);
        
        da = DrawArc.class.cast(ampInst.get(6));
        assertEquals(3, da.getRadius());
        assertEquals(177.9791975971980, da.getStartAngleDegrees(), 1e-9);
        assertEquals(92.02080240280199, da.getEndAngleDegrees(), 1e-9);
        
        da = DrawArc.class.cast(ampInst.get(7));
        assertEquals(3, da.getRadius());
        assertEquals(94.52790391583324, da.getStartAngleDegrees(), 1e-9);
        assertEquals(18.09196103220718, da.getEndAngleDegrees(), 1e-9);
        
        da = DrawArc.class.cast(ampInst.get(8));
        assertEquals(4, da.getRadius());
        assertEquals(18.44951473746869, da.getStartAngleDegrees(), 1e-9);
        assertEquals(-46.52200167332165, da.getEndAngleDegrees(), 1e-9);
        
        da = DrawArc.class.cast(ampInst.get(9));
        assertEquals(45, da.getRadius());
        assertEquals(129.5902480285287, da.getStartAngleDegrees(), 1e-9);
        assertEquals(140.4097519714713, da.getEndAngleDegrees(), 1e-9);
        
        da = DrawArc.class.cast(ampInst.get(10));
        assertEquals(4, da.getRadius());
        assertEquals(-219.8376502707203, da.getStartAngleDegrees(), 1e-9);
        assertEquals(-177.0322473751237, da.getEndAngleDegrees(), 1e-9);
        
        da = DrawArc.class.cast(ampInst.get(11));
        assertEquals(3, da.getRadius());
        assertEquals(-177.4395386204894, da.getStartAngleDegrees(), 1e-9);
        assertEquals(-129.4303590253546, da.getEndAngleDegrees(), 1e-9);
        
        da = DrawArc.class.cast(ampInst.get(12));
        assertEquals(4, da.getRadius());
        assertEquals(-129.8212522621428, da.getStartAngleDegrees(), 1e-9);
        assertEquals(-50.17874773785721, da.getEndAngleDegrees(), 1e-9);
        
        Typeface tf = sf.toTypeface();
        
        Glyph ampGly = tf.getGlyph('&');
        System.out.println("tf capHeight=" + tf.getCapitalHeight());
        System.out.println(ampGly);
        // TODO: Add glyph shape test
    }
    
    @Test
    public void testCapitalB() throws Exception {
        AutoCADShapeFile sf = getISOFont();
        AutoCADShape bShape = sf.getShapes().get('B');
        
        assertNotNull(bShape);
        
        List<ShapeInstruction> bInst = bShape.getInstructions();
        
        assertNotNull(bInst);
        
        // B bytecodes: (* do not generate instructions)
        //      2,                    0 DeactivateDraw
        //      14,8,(-6,-20),        1 VOnly RelativeMoveTo(-6, 20)
        //      8,(1,11),             2 RelativeMoveTo(1,11)
        //      1,                    3 ActivateDraw
        //      8,(5,0),              4 RelativeMoveTo(5,0)
        //      10,(5,-024),          5 QuadrantArc
        //      9,                    * RelativeMoveToArray
        //        (-5,0),             6 RelativeMoveTo(-5,0)
        //        (0,18),             7 RelativeMoveTo(0, 18)
        //        (5,0),              8 RelativeMoveTo(5, 0)
        //        (0,0),              * EndOfArray
        //      10,(4,-024),          9 QuadrantArc
        //      2,                    10 DeactivateDraw
        //      8,(10,-11),           11 RelativeMoveTo(10, -11)
        //      14,8,(-10,-10),       12 VOnly RelativeMoveTo(-10,-10)
        //      0                      * End
        
        Class<?>[] instClasses = new Class[] {
            DeactivateDraw.class, RelativeMoveTo.class, RelativeMoveTo.class,
            ActivateDraw.class, RelativeMoveTo.class, DrawArc.class,
            RelativeMoveTo.class, RelativeMoveTo.class, RelativeMoveTo.class,
            DrawArc.class, DeactivateDraw.class, RelativeMoveTo.class,
            RelativeMoveTo.class
        };

        for (int i = 0; i < instClasses.length; ++i) {
            assertSame(
                "Instruction " + i + " should be a " +
                instClasses[i].getSimpleName() + " object; got " +
                bInst.get(i).getClass().getSimpleName(),
                instClasses[i], bInst.get(i).getClass());
        }

        // Test the quadrant arcs
        DrawArc da = DrawArc.class.cast(bInst.get(5));
        assertEquals(5, da.getRadius());
        assertEquals(90.000000000, da.getStartAngleDegrees(), 1e-6);
        assertEquals(-90.000000000, da.getEndAngleDegrees(), 1e-6);
    }
}
