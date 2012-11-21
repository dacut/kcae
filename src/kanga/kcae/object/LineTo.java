package kanga.kcae.object;

import javax.annotation.Nonnull;

public class LineTo extends SinglePointInstruction {
    public LineTo(long x, long y) {
        super(new Point(x, y));
    }
    
    public LineTo(@Nonnull Point point) {
        super(point);
    }

    @Override
    public void paint(@Nonnull PathPainter pp) {
        pp.lineTo(this.getPoint());
    }
    
    @Override
    @Nonnull
    public LineTo scale(double factor) {
        return new LineTo(this.getPoint().scale(factor));
    }
    
    @Override
    @Nonnull
    public LineTo translate(long dx, long dy) {
        return new LineTo(this.getPoint().translate(dx, dy));
    }
    
    @Override
    @Nonnull
    public LineTo rotateQuadrant(int nQuadrants) {
        return new LineTo(this.getPoint().rotateQuadrant(nQuadrants));
    }
    
    @Override
    @Nonnull
    public String toString() {
        return "LineTo[" + this.getPoint().toString() + "]";
    }
    
    private static final long serialVersionUID = 1L;
}

