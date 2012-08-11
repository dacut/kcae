package kanga.kcae.test;

import junit.framework.TestCase;
import kanga.kcae.object.BezierCurveTo;
import kanga.kcae.object.Point;
import kanga.kcae.object.Rectangle;

import org.junit.Test;

public class TestBezier extends TestCase {
    @Test
    public void testBoundingBox() {
        BezierCurveTo bt = new BezierCurveTo(
            new Point(    0,     0),
            new Point(90000, 90000),
            new Point(20000, 80000));
        
        Rectangle r = bt.updateBoundingBox(new Point(40000, 30000), null);
        assertEquals(Rectangle.fromPoints(20000, 23848, 47630, 80780), r);
    }
}
