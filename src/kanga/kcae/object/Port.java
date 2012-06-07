package kanga.kcae.object;

import java.util.Set;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Port extends CircuitElementAdapter implements Connectable {
    public Port(
        final String name,
        final SignalDirection direction,
        final Net net)
    {
        super(name);
        this.direction = direction;
        this.net = net;
    }

    @Override
    public SignalDirection getDirection() {
        return this.direction;
    }

    @Override
    public void setDirection(final SignalDirection direction) {
        this.direction = direction;
    }

    @Override
    public Net getNet() {
        return this.net;
    }

    @Override
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
    public int compareTo(final CircuitElement otherObj) {
        final Class<? extends Port> myClass = this.getClass();
        final Class<? extends CircuitElement> otherClass = otherObj.getClass();

        if (myClass != otherClass) {
            return myClass.getName().compareTo(otherClass.getName());
        }

        final Port other = myClass.cast(otherObj);

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