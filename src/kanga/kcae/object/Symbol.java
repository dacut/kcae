package kanga.kcae.object;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Symbol implements Shape, Comparable<Symbol> {
    public Symbol(final String name) {
        this.name = name;
        this.shapes = new ShapeGroup();
        return;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    protected Collection<Port> getPortsModifiable() {
        return this.portsByName.values();
    }

    public final Set<Port> getPorts() {
        return new HashSet<Port>(this.getPortsModifiable());
    }

    public ShapeGroup getShapes() {
        return this.shapes;
    }

    public void setShapes(final ShapeGroup shapes) {
        this.shapes = shapes;
    }

    @Override
    public Rectangle getBoundingBox() {
        return this.getShapes().getBoundingBox();
    }

    @Override
    public void setLineStyle(final LineStyle lineStyle) {
        this.getShapes().setLineStyle(lineStyle);
    }

    @Override
    public void setFillStyle(final FillStyle fillStyle) {
        this.getShapes().setFillStyle(fillStyle);
    }

    @Override
    public boolean equals(final Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }

        final Symbol other = (Symbol) otherObj;
        return new EqualsBuilder()
            .append(this.getName(), other.getName())
            .append(this.getShapes(), other.getShapes())
            .append(this.getPortsModifiable(), other.getPortsModifiable())
            .isEquals();
        
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(8271, 61687)
            .append(this.getName())
            .append(this.getShapes())
            .append(this.getPortsModifiable())
            .toHashCode();
    }

    @Override
    public int compareTo(final Symbol other) {
        return new CompareToBuilder()
            .append(this.getName(), other.getName())
            .append(this.getShapes(), other.getShapes())
            .append(this.getPortsModifiable(), other.getPortsModifiable())
            .toComparison();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("name", this.getName())
            .append("shapes", this.getShapes())
            .append("ports", this.getPortsModifiable())
            .toString();
    }

    private String name;
    private ShapeGroup shapes;
    private final Map<String, Port> portsByName = new HashMap<String, Port>();
}