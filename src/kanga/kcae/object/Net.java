package kanga.kcae.object;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import static java.util.Collections.unmodifiableSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Net implements Comparable<Net>, Serializable {
    public Net(@Nonnull String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        
        this.name = name;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    public void setName(@Nonnull String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        
        this.name = name;
    }

    @Nonnull
    public Set<Connectable> getConnections() {
        return unmodifiableSet(this.connections);
    }

    public void addConnection(@Nonnull Connectable connection) {
        if (connection == null) {
            throw new NullPointerException("connection cannot be null");
        }
        
        this.connections.add(connection);
    }
    
    public void removeConnection(@Nonnull Connectable connection) {
        if (connection == null) {
            throw new NullPointerException("connection cannot be null");
        }
        
        this.connections.remove(connection);
    }

    public boolean isConnectedTo(@CheckForNull Connectable connection) {
        return this.connections.contains(connection);
    }

    @Override
    public boolean equals(@CheckForNull Object otherObj) {
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

    @Nonnull
    private String name;
    private final Set<Connectable> connections = new HashSet<Connectable>();
    private static final long serialVersionUID = 1L;
}