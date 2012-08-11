package kanga.kcae.test;

import junit.framework.TestCase;
import kanga.kcae.object.Dimension;
import kanga.kcae.object.Extents;
import kanga.kcae.object.Rectangle;
import kanga.kcae.object.Point;
import org.junit.Test;

public class TestRectangle extends TestCase {
    @Test
    public void testPointOrdering() {
        Rectangle rects[] = new Rectangle[] {
            Rectangle.fromPoints(0, 2, 5, 10),
            Rectangle.fromPoints(5, 10, 0, 2),
            Rectangle.fromPoints(5, 2, 0, 10),
            Rectangle.fromPoints(new Point(0, 2), new Point(5, 10)),
            Rectangle.fromPoints(new Point(5, 2), new Point(0, 10)),
            Rectangle.fromExtents(new Extents.Long(0, 5),
                                  new Extents.Long(2, 10)),
            Rectangle.atOrigin(new Dimension(5, 8)).translate(0, 2),
        };
        
        Rectangle largerRects[] = new Rectangle[] {
            Rectangle.fromPoints(1, 3, 6, 11),
            Rectangle.fromPoints(1, 3, 6, 10),
            Rectangle.fromPoints(1, 3, 5, 11),
            Rectangle.fromPoints(1, 3, 5, 10),
            Rectangle.fromPoints(1, 2, 6, 11),
            Rectangle.fromPoints(1, 2, 6, 10),
            Rectangle.fromPoints(1, 2, 5, 11),
            Rectangle.fromPoints(1, 2, 5, 10),
            Rectangle.fromPoints(0, 3, 6, 11),
            Rectangle.fromPoints(0, 3, 6, 10),
            Rectangle.fromPoints(0, 3, 5, 11),
            Rectangle.fromPoints(0, 3, 5, 10),
            Rectangle.fromPoints(0, 2, 6, 11),
            Rectangle.fromPoints(0, 2, 6, 10),
            Rectangle.fromPoints(0, 2, 5, 11),
        };
        
        Rectangle smallerRects[] = new Rectangle[] {
            Rectangle.fromPoints(-1, 1, 4,  9),
            Rectangle.fromPoints(-1, 1, 4, 10),
            Rectangle.fromPoints(-1, 1, 5,  9),
            Rectangle.fromPoints(-1, 1, 5, 10),
            Rectangle.fromPoints(-1, 2, 4,  9),
            Rectangle.fromPoints(-1, 2, 4, 10),
            Rectangle.fromPoints(-1, 2, 5,  9),
            Rectangle.fromPoints(-1, 2, 5, 10),
            Rectangle.fromPoints( 0, 1, 4,  9),
            Rectangle.fromPoints( 0, 1, 4, 10),
            Rectangle.fromPoints( 0, 1, 5,  9),
            Rectangle.fromPoints( 0, 1, 5, 10),
            Rectangle.fromPoints( 0, 2, 4,  9),
            Rectangle.fromPoints( 0, 2, 4, 10),
            Rectangle.fromPoints( 0, 2, 5,  9),
        };
            
        for (Rectangle r1 : rects) {
            assertEquals(0, r1.getLeft());
            assertEquals(2, r1.getTop());
            assertEquals(5, r1.getRight());
            assertEquals(10, r1.getBottom());
            assertEquals(5, r1.getWidth());
            assertEquals(8, r1.getHeight());
            assertEquals(new Point(0, 2), r1.getTopLeft());
            assertEquals(new Point(0, 10), r1.getBottomLeft());
            assertEquals(new Point(5, 2), r1.getTopRight());
            assertEquals(new Point(5, 10), r1.getBottomRight());
            assertEquals(5.0 / 8.0, r1.getAspectRatio());
            assertEquals(new Point(2, 6), r1.getCenter());
            assertFalse(r1.equals(null));
            assertFalse(r1.equals(new Object()));
            assertEquals("Rectangle[(0, 2), (5, 10)]", r1.toString());
            assertEquals(new Extents.Long(0, 5), r1.getHorizontalExtents());
            assertEquals(new Extents.Long(2, 10), r1.getVerticalExtents());
            
            Rectangle rt = r1.translate(5, 2);
            assertEquals(5, rt.getLeft());
            assertEquals(4, rt.getTop());
            assertEquals(10, rt.getRight());
            assertEquals(12, rt.getBottom());
        
            for (Rectangle r2 : rects) {
                assertEquals(r1, r2);
                assertEquals(r1.hashCode(), r2.hashCode());
                assertEquals(0, r1.compareTo(r2));
            }
            
            for (Rectangle lr : largerRects) {
                assertFalse(r1.equals(lr));
                assertTrue(r1.compareTo(lr) < 0);
                assertTrue(lr.compareTo(r1) > 0);
            }

            for (Rectangle sr : smallerRects) {
                assertFalse(r1.equals(sr));
                assertTrue(r1.compareTo(sr) > 0);
                assertTrue(sr.compareTo(r1) < 0);
            }
        }
    }
    
    @Test
    public void testNullCreation() {
        try {
            Rectangle.fromPoints(null, null);
            fail("Expected a NullPointerException");
        }
        catch (NullPointerException e) { }

        try {
            Rectangle.fromPoints(new Point(0, 0), null);
            fail("Expected a NullPointerException");
        }
        catch (NullPointerException e) { }

        try {
            Rectangle.fromExtents(null, null);
            fail("Expected a NullPointerException");
        }
        catch (NullPointerException e) { }

        try {
            Rectangle.fromExtents(new Extents.Long(0, 5), null);
            fail("Expected a NullPointerException");
        }
        catch (NullPointerException e) { }

        try {
            Rectangle.atOrigin(null);
            fail("Expected a NullPointerException");
        }
        catch (NullPointerException e) { }
    }
    
    @Test
    public void testUnion() {
        Rectangle r1 = Rectangle.fromPoints(0, 0, 0, 0);
        assertEquals(Rectangle.fromPoints(0, 0, 5, 7),
                     r1.union(new Point(5, 7)));
        
        assertEquals(Rectangle.fromPoints(0, 0, 5, 7),
                     r1.union(Rectangle.fromPoints(1, 1, 5, 7)));
        
        assertEquals(Rectangle.fromPoints(-2, -4, 5, 7),
                r1.union(Rectangle.fromPoints(-2, -4, 5, 7)));
    }
    
    @Test
    public void testUnionNull() {
        Rectangle r1 = Rectangle.fromPoints(0, 0, 0, 0);
        
        try {
            r1.union((Point) null);
            fail("Expected a NullPointerException");
        }
        catch (NullPointerException e) { }

        try {
            r1.union((Rectangle) null);
            fail("Expected a NullPointerException");
        }
        catch (NullPointerException e) { }
    }
    
    @Test
    public void testWeirdAspectRatios() {
        assertEquals(Double.POSITIVE_INFINITY,
                     Rectangle.fromPoints(0, 0, 1, 0).getAspectRatio());
        assertTrue(Double.isNaN(
            Rectangle.fromPoints(0, 0, 0, 0).getAspectRatio()));
    }
    
    @Test
    public void testFitToAspectRatio() {
        Rectangle r1 = Rectangle.fromPoints(-10, -10, 10, 10);
        Rectangle r2 = r1.fitToAspect(2.0);
        assertEquals(Rectangle.fromPoints(-12, -6, 12, 6), r2);
    }
    
    @Test
    public void testZoom() {
        Rectangle r1 = Rectangle.fromPoints(-10, -10, 10, 10);
        assertEquals(Rectangle.fromPoints(-20, -20, 20, 20),
                     r1.zoom(2.0));
        assertEquals(Rectangle.fromPoints(-30, -30, 10, 10),
                     r1.zoom(2.0, new Point(10, 10)));
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Test
    public void testCompareToObject() {
        Comparable c = Rectangle.fromPoints(0, 0, 0, 0);
        c.compareTo(Rectangle.fromPoints(1, 2, 3, 4));
        try {
            c.compareTo(new Object());
            fail("Expected a ClassCastException");
        }
        catch (ClassCastException e) { }
    }
}
