package kanga.kcae.object;

import java.io.Serializable;

public class ClosePath implements PathInstruction, Serializable {
    public ClosePath() { }
    
    @Override
    public void paint(PathPainter pp) {
        pp.closePath();
    }
    
    @Override
    public Rectangle updateBoundingBox(
        final Point startPos,
        final Rectangle bbox)
    {
        return bbox;
    }
    
    @Override
    public Point updatePosition(final Point startPos) {
        return null;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }
        
        return true;
    }

    @Override
    public int hashCode() {
        return 22231;
    }

    @Override
    public String toString() {
        return "ClosePath[]";
    }
    
    private static final long serialVersionUID = 1L;
}
    
