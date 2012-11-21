package kanga.kcae.object;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

/** A pin on a {@link Symbol symbol}.
 * 
 *  <p>A pin is one or more connection points (the 
 *  {@link #getPinNumber() pin numbers}) sharing a common
 *  {@link #getName() name} on a symbol.</p>
 *  
 *  <p>Most pins have only one pin number.  The usual exception are power pins
 *  (e.g. VCC, GND), which may have multiple connection points on the device
 *  (if, for example, the device requires more power than can be supplied by a
 *  single wire).  The general expectation is these pins are connected to a
 *  common net outside of the device.</p>
 */
public class Pin implements Comparable<Pin>, Serializable {
    public static class PinNameComparator
        implements Comparator<Pin>, Serializable
    {
        @Override
        public int compare(final Pin pin1, final Pin pin2) {
            return new CompareToBuilder()
                .append(pin1.getName(), pin2.getName())
                .toComparison();
        }
        
        private static final long serialVersionUID = 1L;
    }

    public Pin(
        @CheckForNull final String name,
        @CheckForNull final String pinNumber,
        @Nonnull final SignalDirection signalDirection,
        @Nonnull final Point connectionPoint,
        @Nonnull final Point endPoint,
        @CheckForNull final Collection<PinStyle> pinStyles)
    {
        this(name, pinNumber, signalDirection, connectionPoint, endPoint,
             pinStyles, null, null);
    }
    
    public Pin(
        @CheckForNull final String name,
        @CheckForNull final String pinNumber,
        @Nonnull final SignalDirection signalDirection,
        @Nonnull final Point connectionPoint,
        @Nonnull final Point endPoint)
    {
        this(name, pinNumber, signalDirection, connectionPoint, endPoint,
             null, null, null);
    }
    
    public Pin(
        @CheckForNull final String name,
        @CheckForNull final String pinNumber,
        @Nonnull final SignalDirection signalDirection,
        @Nonnull final Point connectionPoint,
        @Nonnull final Point endPoint,
        @CheckForNull final Collection<PinStyle> pinStyles,
        @CheckForNull final Font nameFont,
        @CheckForNull final Font numberFont)
    {
        this.pinStyles = (
                pinStyles != null ? EnumSet.copyOf(pinStyles) :
                                    EnumSet.noneOf(PinStyle.class));

        this.setName(name);
        this.setPinNumber(pinNumber);
        this.setSignalDirection(signalDirection);
        this.setConnectionPoint(connectionPoint);
        this.setEndPoint(endPoint);
        this.setPinNameFont(nameFont);
        this.setPinNumberFont(numberFont);
    }
    
    @Nonnull
    public String getPinNumber() {
        return this.pinNumber;
    }
    
    public void setPinNumber(@Nonnull final String pinNumber) {
        this.pinNumber = pinNumber;
    }
    
    /** Returns the name of the pin.
     * 
     *  This may be {@code null}.  This is typically used in a transient state
     *  (e.g. when the pin is being constructed), but can be used indefinitely.
     * 
     *  @return The name of the pin.
     */
    @CheckForNull
    public String getName() {
        return this.name;
    }

    /** Sets the name of the pin.
     * 
     *  This may be {@code null}.  This is typically used in a transient state
     *  (e.g. when the pin is being constructed), but can be used indefinitely.
     * 
     *  @param name     The name of the pin.
     */
    public void setName(@CheckForNull final String name) {
        this.name = name;
    }

    /** Returns the signal direction of the pin.
     *  
     *  The signal direction is respect to the device, e.g.
     *  {@link SignalDirection#INPUT INPUT} specifies that this is an input
     *  pin for the device.
     *  
     *  @return The signal direction of the pin.
     */
    @Nonnull
    public SignalDirection getSignalDirection() {
        return this.signalDirection;
    }

    /** Sets the signal direction of the pin.
     *  
     *  The signal direction is respect to the device, e.g.
     *  {@link SignalDirection#INPUT INPUT} specifies that this is an input
     *  pin for the device.
     *  
     *  @param signalDirection  The signal direction of the pin.
     *  @throws NullPointerException if {@code signalDirection} is {@code null}.
     */
    public void setSignalDirection(@Nonnull SignalDirection signalDirection) {
        if (signalDirection == null) {
            throw new NullPointerException("signalDirection cannot be null");
        }
        
        this.signalDirection = signalDirection;
    }

    /** Returns the connection point of the pin.
     * 
     *  This is the point farthest from the device body and opposite the end
     *  point.
     *  
     *  @return The end point of the pin.
     */
    @Nonnull
    public Point getConnectionPoint() {
        return this.connectionPoint;
    }
    
    /** Sets the connection point on the pin.
     * 
     *  This is the point farthest from the device body and opposite the end
     *  point.
     * 
     *  @param connectionPoint The connection point for the pin.
     *  @throws NullPointerException if {@code connectionPoint} is {@code null}. 
     */
    public void setConnectionPoint(@Nonnull final Point connectionPoint) {
        if (connectionPoint == null) {
            throw new NullPointerException("connectionPoint cannot be null");
        }
        
        this.connectionPoint = connectionPoint;
    }

    /** Returns the end point of the pin.
     * 
     *  This is the point closest to the device body and opposite the connection
     *  point.
     *  
     *  @return The end point of the pin.
     */
    @Nonnull
    public Point getEndPoint() {
        return this.endPoint;
    }
    
    /** Sets the end point on this pin.
     * 
     *  This is the point closest to the device body and opposite the connection
     *  point.
     * 
     *  @param endPoint The end point of the pin.
     *  @throws NullPointerException if {@code endPoint} is {@code null}. 
     */
    public void setEndPoint(@Nonnull final Point endPoint) {
        if (endPoint == null) {
            throw new NullPointerException("endPoint cannot be null");
        }
        
        this.endPoint = endPoint;
    }

    /** Returns the set of styles for this pin.
     * 
     *  The resulting set is immutable.
     * 
     *  @return The set of styles for this pin.
     */
    @Nonnull
    public Set<PinStyle> getPinStyles() {
        return unmodifiableSet(this.pinStyles);
    }
    
    /** Adds the specified style to this pin.
     * 
     *  If the style is already present, this is effectively a no-op.
     * 
     *  @param style    The style to add.
     *  @return Whether the style was not previously present on this pin.
     *  @throws NullPointerException if {@code style} is {@code null}.
     */
    public boolean addPinStyle(@Nonnull PinStyle style) {
        if (style == null) {
            throw new NullPointerException("style cannot be null");
        }
        
        return this.pinStyles.add(style);
    }
    
    /** Removes the specified style from this pin.
     * 
     *  If the style is not present, this is effectively a no-op.
     * 
     *  @param style    The style to add.
     *  @return Whether the style was previously present on this pin.
     *  @throws NullPointerException if style is null.
     */
    public boolean removePinStyle(@Nonnull PinStyle style) {
        if (style == null) {
            throw new NullPointerException("style cannot be null");
        }
        
        return this.pinStyles.add(style);
    }
    
    /** Returns the font to use for rendering the pin numbers.
     *  
     *  If {@code null}, the default pin number font for the symbol should
     *  be used.
     *  
     *  @return The font to use for rendering the pin numbers.
     */
    @CheckForNull
    public Font getPinNumberFont() {
        return this.pinNumberFont;
    }

    /** Sets the font to use for rendering the pin numbers.
     *  
     *  If {@code null}, the default pin number font for the symbol should
     *  be used.
     *  
     *  @param pinNumberFont The font to use for rendering the pin numbers.
     */
    public void setPinNumberFont(@CheckForNull Font pinNumberFont) {
        this.pinNumberFont = pinNumberFont;
    }

    /** Returns the font to use for rendering the pin name.
     *  
     *  If {@code null}, the default pin name font for the symbol should
     *  be used.
     *  
     *  @return The font to use for rendering the pin name.
     */
    @CheckForNull
    public Font getPinNameFont() {
        return this.pinNameFont;
    }

    /** Sets the font to use for rendering the pin name.
     *  
     *  If {@code null}, the default pin name font for the symbol should
     *  be used.
     *  
     *  @param pinNameFont The font to use for rendering the pin name.
     */
    public void setPinNameFont(@CheckForNull Font pinNameFont) {
        this.pinNameFont = pinNameFont;
    }

    @Nonnull
    public static List<String> splitPinNumbers(@CheckForNull String pinNumbers)
    {
        if (pinNumbers == null)
            return emptyList();
        
        return asList(pinNumbers.split("[,;] *"));
    }

    @Override
    public boolean equals(final Object otherObj) {
        if (otherObj == null) { return false; }
        if (this == otherObj) { return true; }
        if (this.getClass() != otherObj.getClass()) { return false; }
        
        final Pin other = (Pin) otherObj;
        
        return new EqualsBuilder()
            .append(this.getName(), other.getName())
            .append(this.getPinNumber(), other.getPinNumber())
            .append(this.getSignalDirection(), other.getSignalDirection())
            .append(this.getConnectionPoint(), other.getConnectionPoint())
            .append(this.getEndPoint(), other.getEndPoint())
            .append(this.getPinStyles(), other.getPinStyles())
            .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(this.getName())
            .append(this.getPinNumber())
            .append(this.getSignalDirection())
            .append(this.getConnectionPoint())
            .append(this.getEndPoint())
            .append(this.getPinStyles())
            .toHashCode();
    }
    
    @Override
    public int compareTo(final Pin other) {
        return new CompareToBuilder()
            .append(this.getName(), other.getName())
            .append(this.getPinNumber(), other.getPinNumber())
            .append(this.getSignalDirection(), other.getSignalDirection())
            .append(this.getConnectionPoint(), other.getConnectionPoint())
            .append(this.getEndPoint(), other.getEndPoint())
            .append(this.getPinStyles(), other.getPinStyles())
            .toComparison();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("name", this.getName())
            .append("pinNumber", this.getPinNumber())
            .append("signalDirection", this.getSignalDirection())
            .append("connectionPoint", this.getConnectionPoint())
            .append("endPoint", this.getEndPoint())
            .append("pinStyles", this.getPinStyles())
            .toString();
    }

    @CheckForNull
    private String name;
    
    @Nonnull
    private String pinNumber;
    
    @Nonnull
    private SignalDirection signalDirection;
    
    @Nonnull
    private Point connectionPoint;
    
    @Nonnull
    private Point endPoint;

    @Nonnull
    private EnumSet<PinStyle> pinStyles;
    
    @CheckForNull
    private Font pinNumberFont;
    
    @CheckForNull
    private Font pinNameFont;
    
    private static final long serialVersionUID = 1L;
}
