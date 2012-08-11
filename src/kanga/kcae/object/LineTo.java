package kanga.kcae.object;

import org.apache.commons.lang3.builder.HashCodeBuilder;

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
    public int hashCode() {
        return new HashCodeBuilder(18491, 15675)
            .appendSuper(super.hashCode())
            .toHashCode();
    }

    @Override
    public String toString() {
        return "LineTo[" + this.getPoint().toString() + "]";
    }
}

