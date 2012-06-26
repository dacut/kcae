package kanga.kcae.object;

import static java.lang.Math.round;

public enum BaseUnit {
    inch(25400000, "in"),
    meter(1000000000, "m");
    
    private BaseUnit(final long nanometersPerUnit, final String unitName) {
        this.nanometersPerUnit = nanometersPerUnit;
        this.unitsPerNanometer = 1.0 / (double) nanometersPerUnit;
        this.unitName = unitName;
    }
    
    public double nanometersToUnits(final long nmValue) {
        return this.unitsPerNanometer * nmValue;
    }
    
    public long unitsToNanometers(final double unitValue) {
        return round(this.nanometersPerUnit * unitValue);
    }
    
    public final long nanometersPerUnit;
    public final double unitsPerNanometer;
    public final String unitName;
}
