package kanga.kcae.object;

import java.io.Serializable;
import java.util.Collection;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableSet;

import static com.google.common.collect.Sets.unmodifiableNavigableSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

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
            @CheckForNull final String pinNumbers,
            @Nonnull final SignalDirection signalDirection,
            @Nonnull final Point connectionPoint,
            @Nonnull final Point endPoint)
    {
        this(name, pinNumbers, signalDirection, connectionPoint, endPoint,
             null);
    }

    public Pin(
        @CheckForNull final String name,
        @CheckForNull final String pinNumbers,
        @Nonnull final SignalDirection signalDirection,
        @Nonnull final Point connectionPoint,
        @Nonnull final Point endPoint,
        @CheckForNull final Collection<PinStyle> pinStyles)
    {
        this(name, splitPinNumbers(pinNumbers), signalDirection,
             connectionPoint, endPoint, pinStyles);
    }
    
    public Pin(
        @CheckForNull final String name,
        @CheckForNull final Collection<String> pinNumbers,
        @Nonnull final SignalDirection signalDirection,
        @Nonnull final Point connectionPoint,
        @Nonnull final Point endPoint)
    {
        this(name, pinNumbers, signalDirection, connectionPoint, endPoint,
             null);
    }
    
    public Pin(
        @CheckForNull final String name,
        @CheckForNull final Collection<String> pinNumbers,
        @Nonnull final SignalDirection signalDirection,
        @Nonnull final Point connectionPoint,
        @Nonnull final Point endPoint,
        @CheckForNull final Collection<PinStyle> pinStyles)
    {
        this.pinNumbers = new TreeSet<String>();
        this.pinStyles = (
                pinStyles != null ? EnumSet.copyOf(pinStyles) :
                                    EnumSet.noneOf(PinStyle.class));

        this.setName(name);
        this.setPinNumbers(pinNumbers);
        this.setSignalDirection(signalDirection);
        this.setConnectionPoint(connectionPoint);
        this.setEndPoint(endPoint);
    }
    
    @Nonnull
    public NavigableSet<String> getPinNumbers() {
        return unmodifiableNavigableSet(this.pinNumbers);
    }
    
    public boolean addPinNumber(@Nonnull final String name) {
        if (name == null) {
            throw new NullPointerException("name cannot be null");
        }
        
        return this.pinNumbers.add(name);
    }
    
    public boolean removePinNumber(@Nonnull final String name) {
        return this.pinNumbers.remove(name);
    }

    public void setPinNumbers(
         @CheckForNull final Collection<String> pinNumbers)
    {
        this.pinNumbers.clear();
        if (pinNumbers != null) {
            for (String pinNumber : pinNumbers) {
                if (pinNumber == null) {
                    throw new NullPointerException(
                        "pinNumbers cannot contain null elements");
                }
            }
            this.pinNumbers.addAll(pinNumbers);
        }
    }

    @CheckForNull
    public String getName() {
        return this.name;
    }

    public void setName(@CheckForNull final String name) {
        this.name = name;
    }

    @Nonnull
    public SignalDirection getSignalDirection() {
        return this.signalDirection;
    }

    public void setSignalDirection(@Nonnull SignalDirection signalDirection) {
        if (signalDirection == null) {
            throw new NullPointerException("signalDirection cannot be null");
        }
        
        this.signalDirection = signalDirection;
    }

    @Nonnull
    public Point getConnectionPoint() {
        return this.connectionPoint;
    }
    
    public void setConnectionPoint(@Nonnull final Point connectionPoint) {
        if (connectionPoint == null) {
            throw new NullPointerException("connectionPoint cannot be null");
        }
        
        this.connectionPoint = connectionPoint;
    }

    @Nonnull
    public Point getEndPoint() {
        return this.endPoint;
    }
    
    public void setEndPoint(@Nonnull final Point endPoint) {
        if (endPoint == null) {
            throw new NullPointerException("endPoint cannot be null");
        }
        
        this.endPoint = endPoint;
    }

    @Nonnull
    public Set<PinStyle> getPinStyles() {
        return unmodifiableSet(this.pinStyles);
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
            .append(this.getPinNumbers(), other.getPinNumbers())
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
            .append(this.getPinNumbers())
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
            .append(this.getPinNumbers(), other.getPinNumbers())
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
            .append("pinNumbers", this.getPinNumbers())
            .append("signalDirection", this.getSignalDirection())
            .append("connectionPoint", this.getConnectionPoint())
            .append("endPoint", this.getEndPoint())
            .append("pinStyles", this.getPinStyles())
            .toString();
    }

    @CheckForNull
    private String name;
    
    @Nonnull
    private TreeSet<String> pinNumbers;
    
    private SignalDirection signalDirection;
    private Point connectionPoint;
    private Point endPoint;
    private EnumSet<PinStyle> pinStyles;
    private static final long serialVersionUID = 1L;
}
