package kanga.kcae.object;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Glyph implements Serializable, Comparable<Glyph> {
    public Glyph(
        @Nonnull Path path,
        long defaultAdvance,
        @CheckForNull Map<Character, Long> advance)
    {
        this.path = path;
        this.defaultAdvance = defaultAdvance;
        this.advance = (advance == null ? null :
                        new HashMap<Character, Long>(advance));
    }
    
    public Path getPath() {
        return this.path;
    }
    
    public long getDefaultAdvance() {
        return this.defaultAdvance;
    }
    
    public long getAdvance(char c) {
        if (this.advance != null) {
            Long result = this.advance.get(c);
            if (result != null)
                return result;
        }
        
        return this.getDefaultAdvance();
    }
    
    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (this == otherObj) { return true; }
        if (this.getClass() != otherObj.getClass()) { return false; }
        
        Glyph other = (Glyph) otherObj;
        return new EqualsBuilder()
            .append(this.getPath(), other.getPath())
            .append(this.getDefaultAdvance(), other.getDefaultAdvance())
            .append(this.advance, other.advance)
            .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(this.getPath())
            .append(this.getDefaultAdvance())
            .append(this.advance)
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("path", this.getPath())
            .append("defaultAdvance", this.getDefaultAdvance())
            .append("advance", this.advance)
            .toString();
    }
    
    @Override
    public int compareTo(Glyph other) {
        return new CompareToBuilder()
            .append(this.getPath(), other.getPath())
            .append(this.getDefaultAdvance(), other.getDefaultAdvance())
            .append(this.advance, other.advance)
            .toComparison();
    }
    
    private final Path path;
    private final long defaultAdvance;
    @CheckForNull
    private final Map<Character, Long> advance;
    
    private static final long serialVersionUID = 1L;
}
