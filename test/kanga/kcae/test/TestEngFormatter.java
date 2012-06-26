package kanga.kcae.test;

import org.junit.Test;

import junit.framework.TestCase;
import kanga.kcae.object.EngFormatter;

public class TestEngFormatter extends TestCase {
    @Test
    public void testZero() {
        assertEquals("0 m", EngFormatter.format(0.0, 1, "m"));
        assertEquals("0.0 m", EngFormatter.format(0.0, 2, "m"));
        assertEquals("0.00 m", EngFormatter.format(0.0, 3, "m"));
        assertEquals("0 m", EngFormatter.format(-0.0, 1, "m"));
        assertEquals("0.0 m", EngFormatter.format(-0.0, 2, "m"));
        assertEquals("0.00 m", EngFormatter.format(-0.0, 3, "m"));
    }
    
    @Test
    public void testSigFigs() {
        assertEquals("500 µm", EngFormatter.format(4.64646e-4, 1, "m"));
        assertEquals("460 µm", EngFormatter.format(4.64646e-4, 2, "m"));
        assertEquals("465 µm", EngFormatter.format(4.64646e-4, 3, "m"));
        assertEquals("464.6 µm", EngFormatter.format(4.64646e-4, 4, "m"));
        assertEquals("464.65 µm", EngFormatter.format(4.64646e-4, 5, "m"));
        assertEquals("464.646 µm", EngFormatter.format(4.64646e-4, 6, "m"));
        assertEquals("-500 µm", EngFormatter.format(-4.64646e-4, 1, "m"));
        assertEquals("-460 µm", EngFormatter.format(-4.64646e-4, 2, "m"));
        assertEquals("-465 µm", EngFormatter.format(-4.64646e-4, 3, "m"));
        assertEquals("-464.6 µm", EngFormatter.format(-4.64646e-4, 4, "m"));
        assertEquals("-464.65 µm", EngFormatter.format(-4.64646e-4, 5, "m"));
        assertEquals("-464.646 µm", EngFormatter.format(-4.64646e-4, 6, "m"));
    }
    
    @Test
    public void testSigFigsZero() {
        try {
            EngFormatter.format(4.6464e-4, 0, "");
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e) { }
    }

    @Test
    public void testSigFigsNegative() {
        try {
            EngFormatter.format(4.6464e-4, -1, "");
            fail("Expected an IllegalArgumentException");
        }
        catch (IllegalArgumentException e) { }
    }
    
    @Test
    public void testSmallValues() {
        assertEquals("2e-16 m", EngFormatter.format(1.57e-16, 1, "m", 1e-20));
        assertEquals("1.6e-16 m", EngFormatter.format(1.57e-16, 2, "m", 1e-20));
        assertEquals("1.57e-16 m", EngFormatter.format(1.57e-16, 3, "m", 1e-20));        
        assertEquals("-2e-16 m", EngFormatter.format(-1.57e-16, 1, "m", 1e-20));
        assertEquals("-1.6e-16 m", EngFormatter.format(-1.57e-16, 2, "m", 1e-20));
        assertEquals("-1.57e-16 m", EngFormatter.format(-1.57e-16, 3, "m", 1e-20));        
    }

    @Test
    public void testLargeValues() {
        assertEquals("2e+19 m", EngFormatter.format(1.57e+19, 1, "m"));
        assertEquals("1.6e+19 m", EngFormatter.format(1.57e+19, 2, "m"));
        assertEquals("1.57e+19 m", EngFormatter.format(1.57e+19, 3, "m"));        
        assertEquals("-2e+19 m", EngFormatter.format(-1.57e+19, 1, "m"));
        assertEquals("-1.6e+19 m", EngFormatter.format(-1.57e+19, 2, "m"));
        assertEquals("-1.57e+19 m", EngFormatter.format(-1.57e+19, 3, "m"));        
    }

    @Test
    public void testUnitsNull() {
        assertEquals("2.0 ", EngFormatter.format(2.0, 2, null));
        assertEquals("-2.0 ", EngFormatter.format(-2.0, 2, null));
    }
    
    @Test
    public void testInterspaceNull() {
        assertEquals("2.0", EngFormatter.format(2.0, 2, null, null, 1e-12));
        assertEquals("-2.0", EngFormatter.format(-2.0, 2, null, null, 1e-12));
    }
}
