package kanga.kcae.object;

import java.util.HashSet;
import java.util.Set;
import static java.util.Collections.unmodifiableSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Net implements Comparable<Net> {
    public Net(final String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Set<Connectable> getConnections() {
        return unmodifiableSet(this.connections);
    }

    public void addConnection(Connectable connection) {
        this.connections.add(connection);
    }
    
    public void removeConnection(Connectable connection) {
        this.connections.remove(connection);
    }

    public boolean isConnectedTo(Connectable connection) {
        return this.connections.contains(connection);
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }

        final Net other = (Net) otherObj;
        return new EqualsBuilder()
            .append(this.getName(), other.getName())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(28739, 61799)
            .append(this.getName())
            .toHashCode();
    }

    @Override
    public int compareTo(final Net other) {
        return new CompareToBuilder()
            .append(this.getName(), other.getName())
            .toComparison();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("name", this.getName())
            .toString();
    }

    private String name;
    private transient final Set<Connectable> connections =
        new HashSet<Connectable>();
}