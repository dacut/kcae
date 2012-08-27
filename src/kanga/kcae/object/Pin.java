package kanga.kcae.object;

import java.util.Arrays;
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

public class Pin extends Port {
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
        this.connectionPoint = connectionPoint;
        this.endPoint = endPoint;
        this.pinStyles = (
            pinStyles != null ? EnumSet.copyOf(pinStyles) :
                                EnumSet.noneOf(PinStyle.class));
    }
    
    public static String concatenatePinNumbers(
        final Collection<String> pinNumbers)
    {
        final StringBuilder result = new StringBuilder();
        boolean first = true;
        
        for (final String pinNumber : pinNumbers) {
            if (first) { first = false; }
            else       { result.append(";"); }
            result.append(pinNumber);
        }
        
        return result.toString();
    }
    
    public static Set<String> splitPinNumbers(final String concatPinNumbers) {
        return new HashSet<String>(Arrays.asList(concatPinNumbers.split(";")));
    }

    public Set<String> getPinNumbers() {
        return splitPinNumbers(super.getName());
    }
    
    public boolean addPinNumber(final String name) {
        final Set<String> names = splitPinNumbers(this.getName());
        final boolean result = names.add(name);
        this.setName(concatenatePinNumbers(names));
        return result;
    }
    
    public boolean removePinNumber(final String name) {
        final Set<String> names = splitPinNumbers(this.getName());
        final boolean result = names.remove(name);
        this.setName(concatenatePinNumbers(names));
        return result;
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
        final boolean superResult = super.equals(otherObj);        
        final Pin other = (Pin) otherObj;
        
        return new EqualsBuilder()
            .appendSuper(superResult)
            .append(this.getConnectionPoint(), other.getConnectionPoint())
            .append(this.getEndPoint(), other.getEndPoint())
            .append(this.getPinStyles(), other.getPinStyles())
            .isEquals();
    }
    
    @Override
    public int hashCode() {
        final int superResult = super.hashCode();
        return new HashCodeBuilder(17, 37)
            .appendSuper(superResult)
            .append(this.getConnectionPoint())
            .append(this.getEndPoint())
            .append(this.getPinStyles())
            .toHashCode();
    }
    
    @Override
    public int compareTo(final CircuitElement otherCE) {
        final int superResult = super.compareTo(otherCE);
        if (superResult != 0) {
            return superResult;
        }

        final Pin other;
        try {
            other = (Pin) otherCE;
        }
        catch (ClassCastException e) {
            return this.getClass().getName().compareTo(
                otherCE.getClass().getName());
        }
        
        return new CompareToBuilder()
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
    
    private final Point connectionPoint;
    private final Point endPoint;
    private final EnumSet<PinStyle> pinStyles;
    private static final long serialVersionUID = 1L;
}
