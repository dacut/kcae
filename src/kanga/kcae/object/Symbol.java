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
public class Symbol implements Comparable<Symbol>, Serializable {
    /** Create a new symbol.
     * 
     *  @param  name    The name of the symbol.
     */
    public Symbol(
        @CheckForNull final String name)
    {
        this(name, null, null);
    }

    /** Create a new symbol.
     * 
     *  @param  name    The name of the symbol.
     *  @param  pinNameFont The default font to use for pin names (if not
     *                  specified on the pin itself).
     *  @param  pinNumberFont The default font to use for pin numbers (if not
     *                  specified on the pin itself).
     */
    public Symbol(
        @CheckForNull final String name,
        @CheckForNull final Font pinNameFont,
        @CheckForNull final Font pinNumberFont)
    {
        this.name = name;
        this.shapes = new ShapeGroup();
        this.pinNameFont = pinNameFont;
        this.pinNumberFont = pinNumberFont;
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
    
    /** Returns an immutable navigable set of the pins on this device, sorted
     *  by name.
     * 
     *  @return A set of the pins on this device.
     */
    @Nonnull
    @JsonProperty
    public Set<Pin> getPins() {
        return unmodifiableSet(this.pins);
    }
    
    /** Sets the pins on the device.
     * 
     *  @param  pins    A set of pins on the device.  This may be {@code null},
     *                  in which case the device will contain no pins.  However,
     *                  if this is not {@code null}, it may <em>not</em> contain
     *                  {@code null} elements.
     *                  
     *  @throws NullPointerException If {@code pins} is not {@code null} but
     *          contains a {@code null} element.
     */
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

    /** The default font to use for drawing pin names.
     * 
     *  This font is used if a pin does not specify its own font.
     *  
     *  @return The default pin name font.
     */
    @Nonnull
    public Font getPinNameFont() {
        if (this.pinNameFont == null) {
            return FailsafeDefaults.ISO_FONT_8MM;
        } else {
            return this.pinNameFont;
        }
    }

    /** Sets the default font to use for drawing pin names.
     * 
     *  @param  pinNameFont The default pin name font.  If {@code null}, an
     *                  application default is used.
     */
    public void setPinNameFont(@CheckForNull Font pinNameFont) {
        this.pinNameFont = pinNameFont;
    }

    /** The font to use for drawing the specified pin's name.
     * 
     *  @param  pin     The pin being drawn.
     *  
     *  @return The font to use 
     */
    @Nonnull
    public Font getPinNameFont(@Nonnull final Pin pin) {
        final Font pinFont = pin.getPinNameFont();
        if (pinFont != null) {
            return pinFont;
        } else {
            return this.getPinNameFont();
        }
    }

    /** The default font to use for drawing pin numbers.
     * 
     *  This font is used if a pin does not specify its own font.
     * 
     *  @return The default pin number font.
     */
    public Font getPinNumberFont() {
        if (this.pinNumberFont == null) {
            return FailsafeDefaults.ISO_FONT_8MM;
        } else {
            return this.pinNumberFont;
        }
    }

    /** The font to use when drawing the specified pin's number.
     * 
     *  @param  pin     The pin being drawn.
     *  
     *  @return The font to use 
     */
    @Nonnull
    public Font getPinNumberFont(@Nonnull final Pin pin) {
        final Font pinFont = pin.getPinNumberFont();
        if (pinFont != null) {
            return pinFont;
        } else {
            return this.getPinNumberFont();
        }
    }
    
    
    /** Sets the default font to use for drawing pin numbers.
     * 
     *  @param  pinNumberFont The default pin number font.  If {@code null}, an
     *                  application default is used.
     */
    public void setPinNumberFont(@CheckForNull Font pinNumberFont) {
        this.pinNumberFont = pinNumberFont;
    }

    /** Returns the shapes which make up the symbol.
     * 
     *  The returned {@link ShapeGroup} is shared with the symbol: any
     *  modifications made are actually being made on the symbol itself.
     *  
     *  @return The shapes which make up the symbol.
     */
    @JsonProperty
    @Nonnull
    public ShapeGroup getShapes() {
        return this.shapes;
    }

    /** Sets the shapes which make up the symbol.
     * 
     *  The symbol will take ownership of the shape group passed in.
     * 
     *  @param  shapes  The shapes which make up the symbol.  If {@code null},
     *                  an empty shape group is created.
     */
    @JsonProperty
    public void setShapes(@CheckForNull final ShapeGroup shapes) {
        if (shapes == null) {
            this.shapes = new ShapeGroup();
        } else {
            this.shapes = shapes;
        }
    }

    @CheckForNull
    public Rectangle getBoundingBox() {
        return this.getShapes().getBoundingBox();
    }

    public void setLineStyle(final LineStyle lineStyle) {
        this.getShapes().setLineStyle(lineStyle);
    }

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
    
    @CheckForNull
    private Font pinNameFont;
    
    @CheckForNull
    private Font pinNumberFont;
    
    @Nonnull
    private final Set<Pin> pins =
        newSetFromMap(new IdentityHashMap<Pin, Boolean>());
    
    private static final long serialVersionUID = 1L;
}