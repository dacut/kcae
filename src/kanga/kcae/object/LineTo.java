package kanga.kcae.object;

public class LineTo extends SinglePointInstruction {
    public LineTo(final long x, final long y) {
        super(new Point(x, y));
    }
    
    public LineTo(final Point point) {
        super(point);
    }

    @Override
    public void paint(PathPainter pp) {
        pp.lineTo(this.getPoint());
    }
    
    @Override
    public String toString() {
        return "LineTo[" + this.getPoint().toString() + "]";
    }
    
    private static final long serialVersionUID = 1L;
}

