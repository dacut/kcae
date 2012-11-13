package kanga.kcae.xchg.autocad;

import java.io.Serializable;

public abstract class ShapeInstruction implements Serializable {
    public ShapeInstruction(boolean verticalOnly) {
        this.verticalOnly = verticalOnly;
    }
    
    public boolean verticalOnly() {
        return this.verticalOnly;
    }
    
    private final boolean verticalOnly;
    private static final long serialVersionUID = 1L;
}
