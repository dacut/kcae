package kanga.kcae.object.event;

import java.io.Serializable;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public final class PropertyChangeEvent<T>
    implements Comparable<PropertyChangeEvent<T>>, Serializable
{
    public PropertyChangeEvent(String propertyName, T oldValue, T newValue) {
        this.propertyName = propertyName;
        this.oldValue = oldValue;
        this.newValue = newValue;
        return;
    }
    
    public String getPropertyName() {
        return this.propertyName;
    }
    
    public T getOldValue() {
        return this.oldValue;
    }
    
    public T getNewValue() {
        return this.newValue;
    }
    
    @Override
    public int compareTo(PropertyChangeEvent<T> other) {
        return new CompareToBuilder()
            .append(this.getPropertyName(), other.getPropertyName())
            .append(this.getOldValue(), other.getOldValue())
            .append(this.getNewValue(), other.getNewValue())
            .toComparison();
    }
    
    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (this == otherObj) { return true; }
        if (this.getClass() != otherObj.getClass()) { return false; }
        
        PropertyChangeEvent<?> other = (PropertyChangeEvent<?>) otherObj;
        return new EqualsBuilder()
            .append(this.getPropertyName(), other.getPropertyName())
            .append(this.getOldValue(), other.getOldValue())
            .append(this.getNewValue(), other.getNewValue())
            .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(this.getPropertyName())
            .append(this.getOldValue())
            .append(this.getNewValue())
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("propertyName", this.getPropertyName())
            .append("oldValue", this.getOldValue())
            .append("newValue", this.getNewValue())
            .toString();
    }
    
    final String propertyName;
    final T oldValue;
    final T newValue;

    private static final long serialVersionUID = 1L;
}
