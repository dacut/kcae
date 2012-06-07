package kanga.kcae.object;

public class ClosePath implements PathInstruction {
    public ClosePath() { }
    
    @Override
    public void paint(PathPainter pp) {
        pp.closePath();
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
}
    
