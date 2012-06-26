/**
 * 
 */
package kanga.kcae.test;

import static java.lang.System.setProperty;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import kanga.kcae.object.BaseUnit;

public class TestBaseUnit extends TestCase {
    @Override
    @Before
    public void setUp() {
        setProperty("org.apache.commons.logging.Log",
                    "org.apache.commons.logging.impl.Log4JLogger");
    }
    
    @Test
    public void testInch() {
        assertEquals(0.0, BaseUnit.inch.nanometersToUnits(0), 1e-12);
        assertEquals(1.0, BaseUnit.inch.nanometersToUnits(25400000), 1e-12);
        assertEquals(-0.1, BaseUnit.inch.nanometersToUnits(-2540000), 1e-12);
        assertEquals(0, BaseUnit.inch.unitsToNanometers(0.0));
        assertEquals(25400000, BaseUnit.inch.unitsToNanometers(1.0));
        assertEquals(-2540000, BaseUnit.inch.unitsToNanometers(-0.1));
    }
    
    @Test
    public void testMeter() {
        assertEquals(0.0, BaseUnit.meter.nanometersToUnits(0));
        assertEquals(1.0, BaseUnit.meter.nanometersToUnits(1000000000));
        assertEquals(-0.1, BaseUnit.meter.nanometersToUnits(-100000000));
        assertEquals(0, BaseUnit.meter.unitsToNanometers(0.0));
        assertEquals(1000000000, BaseUnit.meter.unitsToNanometers(1.0));
        assertEquals(-100000000, BaseUnit.meter.unitsToNanometers(-0.1));
    }
}
