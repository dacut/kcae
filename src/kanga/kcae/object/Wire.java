package kanga.kcae.object;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Wire implements Comparable<Wire>, Serializable {
    public Wire(final Point start, final Point end) {
        this(start, end, null);
    }

    public Wire(final Point start, final Point end, final Net net) {
        this.start = start;
        this.end = end;
        this.net = net;
    }

    public Point getStartPoint() {
        return this.start;
    }

    public void setStartPoint(final Point start) {
        this.start = start;
    }

    public Point getEndPoint() {
        return this.end;
    }

    public void setEndPoint(Point end) {
        this.end = end;
    }

    public Net getNet() {
        return this.net;
    }

    public void setNet(Net net) {
        this.net = net;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }

        final Wire other = (Wire) otherObj;
        return new EqualsBuilder().appendSuper(super.equals(other))
            .append(this.start, other.start)
            .append(this.end, other.end)
            .append(this.net, other.net)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(53955, 43939)
            .append(this.start)
            .append(this.end)
            .append(this.net)
            .toHashCode();
    }

    @Override
    public int compareTo(Wire other) {
        return new CompareToBuilder()
            .append(this.start, other.start)
            .append(this.end, other.end)
            .append(this.net, other.net)
            .toComparison();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("startPoint", this.start)
            .append("endPoint", this.end)
            .append("net", this.net)
            .toString();
    }

    private Point start;
    private Point end;
    private Net net;
    private static final long serialVersionUID = 1L;
}