package kanga.kcae.object;

import java.io.Serializable;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Collections.newSetFromMap;
import static java.util.Collections.unmodifiableSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.codehaus.jackson.annotate.JsonProperty;

/** A device symbol for use in the schematic editor.
 */
public class Symbol implements Shape, Comparable<Symbol>, Serializable {
    /** Create a new symbol.
     * 
     *  @param name     The name of the symbol.
     */
    public Symbol(@Nullable final String name) {
        this.name = name;
        this.shapes = new ShapeGroup();
        return;
    }

    /** Returns the name of the symbol.
     * 
     *  @return The name of the symbol.
     */
    @JsonProperty
    @CheckForNull
    public String getName() {
        return this.name;
    }

    /** Sets the name of the symbol.
     * 
     *  @param  name    The new name of the symbol.
     */
    @JsonProperty
    public void setName(@Nullable final String name) {
        this.name = name;
    }

    public boolean addPin(@Nonnull final Pin pin) {
        if (pin == null) {
            throw new NullPointerException("pin cannot be null");
        }
        
        return this.pins.add(pin);
    }
    
    /** Returns a navigable set of the pins on this device (sorted by name).
     * 
     *  @return A set of the pins on this device.
     */
    @Nonnull
    @JsonProperty
    public Set<Pin> getPins() {
        return unmodifiableSet(this.pins);
    }
    
    public void setPins(@CheckForNull final Collection<Pin> pins) {
        if (pins != null) {
            for (final Pin pin : pins) {
                if (pin == null) {
                    throw new NullPointerException(
                        "pins cannot contain null elements.");
                }
            }
        }
        
        this.pins.clear();
        
        if (pins != null) {
            this.pins.addAll(pins);
        }
        
        return;
    }

    @JsonProperty
    @Nonnull
    public ShapeGroup getShapes() {
        return this.shapes;
    }

    @JsonProperty
    public void setShapes(@CheckForNull final ShapeGroup shapes) {
        if (shapes == null) {
            this.shapes = new ShapeGroup();
        } else {
            try {
                this.shapes = shapes.clone();
            }
            catch (final CloneNotSupportedException e) {
                throw new RuntimeException("ShapeGroup " + shapes +
                    " does not support clone?", e);
            }
        }
    }

    @Override
    @CheckForNull
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
    public boolean equals(@CheckForNull final Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }

        final Symbol other = (Symbol) otherObj;
        return new EqualsBuilder()
            .append(this.getName(), other.getName())
            .append(this.getShapes(), other.getShapes())
            .append(this.getPins(), other.getPins())
            .isEquals();
        
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(8271, 61687)
            .append(this.getName())
            .append(this.getShapes())
            .append(this.getPins())
            .toHashCode();
    }

    @Override
    public int compareTo(final Symbol other) {
        return new CompareToBuilder()
            .append(this.getName(), other.getName())
            .append(this.getShapes(), other.getShapes())
            .append(this.getPins(), other.getPins())
            .toComparison();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("name", this.getName())
            .append("shapes", this.getShapes())
            .append("pins", this.getPins())
            .toString();
    }

    @CheckForNull
    private String name;
    
    @Nonnull
    private ShapeGroup shapes;
    
    @Nonnull
    private final Set<Pin> pins =
        newSetFromMap(new IdentityHashMap<Pin, Boolean>());
    
    private static final long serialVersionUID = 1L;
}