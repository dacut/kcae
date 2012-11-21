package kanga.kcae.object;

import javax.annotation.Nonnull;

public class MoveTo extends SinglePointInstruction {
    public MoveTo(long x, final long y) {
        super(new Point(x, y));
    }
    
    public MoveTo(@Nonnull Point point) {
        super(point);
    }
    
    @Override
    public void paint(@Nonnull PathPainter pp) {
        pp.moveTo(this.getPoint());
    }
    
    @Override
    @Nonnull
    public MoveTo scale(double factor) {
        return new MoveTo(this.getPoint().scale(factor));
    }
    
    @Override
    @Nonnull
    public MoveTo translate(long dx, long dy) {
        return new MoveTo(this.getPoint().translate(dx, dy));
    }
    
    @Override
    @Nonnull
    public MoveTo rotateQuadrant(int nQuadrants) {
        return new MoveTo(this.getPoint().rotateQuadrant(nQuadrants));
    }
    
    @Override
    @Nonnull
    public String toString() {
        return "MoveTo[" + this.getPoint().toString() + "]";
    }
    
    private static final long serialVersionUID = 1L;
}

