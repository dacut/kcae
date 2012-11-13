package kanga.kcae.xchg.autocad;

public enum DirectionCode {
    // Important! Do not reorder these enums; we depend on
    // their ordinal position matching AutoCAD's encodings.
    E  ( 1.0,  0.0),
    ENE( 1.0,  0.5),
    NE ( 1.0,  1.0),
    NNE( 0.5,  1.0),
    N  ( 0.0,  1.0),
    NNW(-0.5,  1.0),
    NW (-1.0,  1.0),
    WNW(-1.0,  0.5),
    W  (-1.0,  0.0),
    WSW(-1.0, -0.0),
    SW (-1.0, -1.0),
    SSW(-0.5, -1.0),
    S  ( 0.0, -1.0),
    SSE( 0.5, -1.0),
    SE ( 1.0, -1.0),
    ESE( 1.0, -0.5);
    
    DirectionCode(double relX, double relY) {
        this.relX = relX;
        this.relY = relY;
    }
    
    public static DirectionCode forCode(final int code) {
        if (code < 0 || code > 15) {
            throw new IllegalArgumentException(
                "Invalide code " + code + "; must be 0-15");
        }
        
        return codeToEnum[code];
    }
    
    public final double relX;
    public final double relY;
    private static final DirectionCode[] codeToEnum;
    
    static {
        codeToEnum = new DirectionCode[16];
        for (final DirectionCode dc : DirectionCode.values()) {
            codeToEnum[dc.ordinal()] = dc;
        }
    }
}