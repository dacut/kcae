package kanga.kcae.test;

import java.util.List;
import static java.lang.Math.abs;

import junit.framework.TestCase;
import org.junit.Test;

import kanga.kcae.object.ArcTo;
import kanga.kcae.object.PathPainter;
import kanga.kcae.object.Point;

public class TestArc extends TestCase {
    public static void checkBezier(
        final long ax,
        final long ay,
        final long ox,
        final long oy,
        final double includedAngle,
        final long cp1x,
        final long cp1y,
        final long cp2x,
        final long cp2y,
        final long endx,
        final long endy)
    {
        ObjectCallHistory history = new ObjectCallHistory();
        PathPainter pp = history.getProxy(PathPainter.class);
        
        ArcTo.paintAsBezier(pp, new Point(ax, ay), new Point(ox, oy),
                            includedAngle);
        
        List<ObjectCallHistory.Call> calls = history.getCallHistory();
        assertEquals(1, calls.size());
        ObjectCallHistory.Call bezierTo = calls.get(0);
        assertEquals("bezierCurveTo", bezierTo.methodName);
        assertEquals(3, bezierTo.args.length);
        
        Point cp1 = Point.class.cast(bezierTo.args[0]);
        Point cp2 = Point.class.cast(bezierTo.args[1]);
        Point end = Point.class.cast(bezierTo.args[2]);
        
        assertTrue(abs(cp1x - cp1.getX()) < 2);
        assertTrue(abs(cp1y - cp1.getY()) < 2);
        assertTrue(abs(cp2x - cp2.getX()) < 2);
        assertTrue(abs(cp2y - cp2.getY()) < 2);
        assertTrue(abs(endx - end.getX()) < 2);
        assertTrue(abs(endy - end.getY()) < 2);
    }
    
    @Test
    public void testArcToBezier() {
        checkBezier(1000000, 0, 0, 0, 0.5, 1000000, 167540, 957906, 332395,
                    877583, 479426);
    }
}
