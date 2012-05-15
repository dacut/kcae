package kanga.kcae.object;

import java.util.Set;

import org.apache.commons.lang3.builder.CompareToBuilder;
Pimport org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Port
    extends CircuitElementAdapter
    implements Connectable, Comparable<Port>
{
    public Port(
        final String name,
        final SignalDirection direction,
        final Net net)
    {
        super(name);
        this.direction = direction;
        this.net = net;
    }

    public SignalDirection getDirection() {
        return this.direction;
    }

    public void setDirection(final SignalDirection direction) {
        this.direction = direction;
    }

    public Net getNet() {
        return this.net;
    }

    public void setNet(final Net net) {
        this.net = net;
    }

    @Override
    public void getNetsInto(final Set<Net> nets) {
        nets.add(this.getNet());
    }

    @Override
    public boolean equals(final Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (this.getClass() != otherObj.getClass()) { return false; }

        final Port other = (Port) otherObj;
        return new EqualsBuilder().appendSuper(super.equals(other))
            .append(this.getDirection(), other.getDirection())
            .append(this.getNet(), other.getNet())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(21395, 45731).appendSuper(super.hashCode())
            .append(this.getDirection())
            .append(this.getNet())
            .toHashCode();
    }

    @Override
    public int compareTo(final Port other) {
        return new CompareToBuilder()
            .append(this.getName(), other.getName())
            .append(this.getDirection(), other.getDirection())
            .append(this.getNet(), other.getNet())
            .toComparison();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("name", this.getName())
            .append("direction", this.getDirection())
            .append("net", this.getNet())
            .toString();
    }

    private SignalDirection direction;
    private Net net;
}