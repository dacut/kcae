package kanga.kcae.object;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public abstract class CircuitElementAdapter implements CircuitElement {
    public CircuitElementAdapter(final String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (! CircuitElement.class.isInstance(otherObj)) { return false; }

        final CircuitElement other = CircuitElement.class.cast(otherObj);
        return this.getName().equals(other.getName());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1401, 36705)
            .append(this.getName())
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("name", this.getName())
            .toString();
    }

    private String name;
    private static final long serialVersionUID = 1L;
}
