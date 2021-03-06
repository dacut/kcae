package kanga.kcae.object;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import static java.util.Collections.unmodifiableSet;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/** Representation of a circuit schematic.
 */
public class Schematic implements Comparable<Schematic>, Serializable {
    private static final long serialVersionUID = 1L;

    public Schematic() { }
    
    public Set<CircuitElement> getElements() {
        return unmodifiableSet(this.elements);
    }

    public <T extends CircuitElement> Set<T> getElements(final Class<T> clazz) {
        final Set<T> result = new HashSet<T>(this.elements.size());
        for (final CircuitElement ce : this.elements) {
            if (clazz.isInstance(ce)) {
                result.add(clazz.cast(ce));
            }
        }

        return result;
    }

    public void addElement(final CircuitElement element) {
        if (element == null) {
            throw new NullPointerException("element cannot be null.");
        }

        if (this.elements.contains(element)) {
            throw new IllegalArgumentException(
                "element is already present in this schematic.");
        }

        this.elements.add(element);
    }

    public void removeElement(final CircuitElement element) {
        if (element == null) {
            throw new NullPointerException("element cannot be null.");
        }

        if (! this.elements.remove(element)) {
            throw new IllegalArgumentException(
                "element is not present in this schematic.");
        }
    }

    public Set<Wire> getWires() {
        return unmodifiableSet(this.wires);
    }

    public void addWire(final Wire wire) {
        if (wire == null) {
            throw new NullPointerException("wire cannot be null.");
        }

        if (this.wires.contains(wire)) {
            throw new IllegalArgumentException(
                "wire is already present in this schematic.");
        }

        this.wires.add(wire);
    }

    public void removeWire(final Wire wire) {
        if (wire == null) {
            throw new NullPointerException("wire cannot be null.");
        }

        if (! this.wires.remove(wire)) {
            throw new IllegalArgumentException(
                "wire is not present in this schematic.");
        }
    }

    public Set<Net> getNets() {
        final Set<Net> result = new HashSet<Net>();
        this.getNetsInto(result);
        return result;
    }

    public void getNetsInto(final Set<Net> nets) {
        for (final CircuitElement ce : this.elements) {
            ce.getNetsInto(nets);
        }

        for (final Wire wire : this.wires) {
            nets.add(wire.getNet());
        }
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }

        final Schematic other = (Schematic) otherObj;
        if (! this.getElements().equals(other.getElements())) { return false; }
        if (! this.getWires().equals(other.getWires())) { return false; }
        if (! this.getNets().equals(other.getNets())) { return false; }
        
        return true;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(27901, 48967)
            .append(this.getElements())
            .append(this.getWires())
            .append(this.getNets())
            .toHashCode();
    }

    @Override
    public int compareTo(Schematic other) {
        final Iterator<CircuitElement> cels1, cels2;

        cels1 = this.getElements().iterator();
        cels2 = other.getElements().iterator();

        while (cels1.hasNext() && cels2.hasNext()) {
            final CircuitElement cel1 = cels1.next();
            final CircuitElement cel2 = cels2.next();
            final int result = cel1.compareTo(cel2);

            if (result != 0) { return result; }
        }

        if      (cels1.hasNext()) { return +1; }
        else if (cels2.hasNext()) { return -1; }

        return 0;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("elements", this.elements)
            .append("wires", this.wires)
            .toString();
    }

    private final Set<CircuitElement> elements = new HashSet<CircuitElement>();
    private final Set<Wire> wires = new HashSet<Wire>();
}