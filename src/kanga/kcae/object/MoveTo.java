package kanga.kcae.object;

import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MoveTo extends SinglePointInstruction {
    public MoveTo(final Point point) {
        super(point);
    }

    @Override
    public void paint(PathPainter pp) {
        pp.moveTo(this.getPoint());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(18491, 15675)
            .appendSuper(super.hashCode())
            .toHashCode();
    }

    @Override
    public String toString() {
        return "MoveTo[" + this.getPoint().toString() + "]";
    }
}

