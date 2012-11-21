package kanga.kcae.object;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nullable;

public class ClosePath implements PathInstruction, Serializable {
    public ClosePath() { }
    
    @Override
    public void paint(PathPainter pp) {
        pp.closePath();
    }
    
    @Override
    @Nullable
    public Rectangle updateBoundingBox(
        @Nullable @CheckForNull Point startPos,
        @Nullable @CheckForNull Rectangle bbox)
    {
        return bbox;
    }
    
    @Override
    @Nullable
    public Point updatePosition(@Nullable @CheckForNull Point startPos) {
        return null;
    }
    
    @Override
    public ClosePath scale(double factor) {
        return this;
    }
    
    @Override
    public ClosePath translate(long dx, long dy) {
        return this;
    }
    
    @Override
    public ClosePath rotateQuadrant(int nQuadrants) {
        return this;
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
    
