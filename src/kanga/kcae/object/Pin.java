package kanga.kcae.object;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Collections.unmodifiableSet;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Pin extends Port implements Comparable<Pin> {
    public Pin(
        @Nullable final Collection<String> pinNumbers,
        @Nonnull final SignalDirection signalDirection,
        @Nullable final Net net,
        @Nonnull final Point connectionPoint,
        @Nonnull final Point endPoint)
    {
        this(pinNumbers, signalDirection, net, connectionPoint, endPoint, null);
    }
    
    public Pin(
        @Nullable final Collection<String> pinNumbers,
        @Nonnull final SignalDirection signalDirection,
        @Nullable final Net net,
        @Nonnull final Point connectionPoint,
        @Nonnull final Point endPoint,
        @Nullable final Collection<PinStyle> pinStyles)
    {
        super(concatenatePinNumbers(pinNumbers), signalDirection, net);
        this.pinNumbers = (
            pinNumbers != null ? new HashSet<String>(pinNumbers) :
                                 new HashSet<String>());
        this.connectionPoint = connectionPoint;
        this.endPoint = endPoint;
        this.pinStyles = (
            pinStyles != null ? EnumSet.copyOf(pinStyles) :
                                EnumSet.noneOf(PinStyle.class));
    }
    
    public static String concatentatePinNumbers(
        final Collection<String> pinNumbers)
    {
        final StringBuilder result = new StringBuilder();
    }

    public Set<String> getPinNumbers() {
        return unmodifiableSet(this.pinNumbers);
    }
    
    public boolean addPinNumber(final String name) {
        return this.pinNumbers.add(name);
    }
    
    public boolean removePinNumber(final String name) {
        return this.pinNumbers.remove(name);
    }
    
    @Override
    public SignalDirection getSignalDirection() {
        return this.signalDirection;
    }
    
    @Override
    public void setSignalDirection(
        @Nonnull final SignalDirection signalDirection)
    {
        this.signalDirection = signalDirection;
    }
    
    @Override
    public Net getNet() {
        return this.net;
    }
    
    @Override
    public void setNet(Net net) {
        this.net = net;
    }

    public Point getConnectionPoint() {
        return this.connectionPoint;
    }

    public Point getEndPoint() {
        return this.endPoint;
    }

    public Set<PinStyle> getPinStyles() {
        return unmodifiableSet(this.pinStyles);
    }

    @Override
    public boolean equals(final Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (this.getClass() != otherObj.getClass()) { return false; }
        
        final Pin other = (Pin) otherObj;
        
        return new EqualsBuilder()
            .append(this.getPinNumbers(), other.getPinNumbers())
            .append(this.getSignalDirection(), other.getSignalDirection())
            .append(this.getNet(), other.getNet())
            .append(this.getConnectionPoint(), other.getConnectionPoint())
            .append(this.getEndPoint(), other.getEndPoint())
            .append(this.getPinStyles(), other.getPinStyles())
            .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(this.getPinNumbers())
            .append(this.getSignalDirection())
            .append(this.getNet())
            .append(this.getConnectionPoint())
            .append(this.getEndPoint())
            .append(this.getPinStyles())
            .toHashCode();
    }
    
    @Override
    public int compareTo(final Pin other) {
        return new CompareToBuilder()
            .append(this.getPinNumbers(), other.getPinNumbers())
            .append(this.getSignalDirection(), other.getSignalDirection())
            .append(this.getNet(), other.getNet())
            .append(this.getConnectionPoint(), other.getConnectionPoint())
            .append(this.getEndPoint(), other.getEndPoint())
            .append(this.getPinStyles(), other.getPinStyles())
            .toComparison();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("pinNames", this.getPinNumbers())
            .append("signalDirection", this.getSignalDirection())
            .append("net", this.getNet())
            .append("connectionPoint", this.getConnectionPoint())
            .append("endPoint", this.getEndPoint())
            .append("pinStyles", this.getPinStyles())
            .toString();
    }
    
    private final Set<String> pinNumbers;
    private SignalDirection signalDirection;
    private Net net;
    private final Point connectionPoint;
    private final Point endPoint;
    private final EnumSet<PinStyle> pinStyles;
    private static final long serialVersionUID = 1L;
}
