package kanga.kcae.object;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Font implements Serializable, Comparable<Font> {
    private static final Log log = LogFactory.getLog(Font.class);
    public Font(@Nonnull Typeface typeface, long capitalHeight) {
        if (typeface == null) {
            throw new NullPointerException("typeface cannot be null");
        }
        
        this.typeface = typeface;
        this.scale = ((double) capitalHeight) /
                     ((double) typeface.getCapitalHeight());
        return;
    }
    
    @Nonnull
    public Shape render(@CheckForNull CharSequence text) {
        ShapeGroup result = new ShapeGroup();
        long x = 0;
        Glyph lastGlyph = null;
        
        text = defaultIfNull(text, ""); 
        
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            Glyph glyph = this.getTypeface().getGlyph(c);

            if (lastGlyph != null) {
                x += lastGlyph.getAdvance(c) * this.getScale();
            }
            
            Path glyphPath = glyph.getPath().scale(this.getScale()).
                translate(x, 0);
            
            log.debug("Adding glyphPath " + System.identityHashCode(glyphPath));

            result.addShape(glyphPath);
            lastGlyph = glyph;
        }
        
        return result;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    @Nonnull
    public Typeface getTypeface() {
        return this.typeface;
    }
    
    @Override
    public boolean equals(@CheckForNull Object otherObj) {
        if (otherObj == null)                       { return false; }
        if (this == otherObj)                       { return true; }
        if (this.getClass() != otherObj.getClass()) { return false; }
        
        Font other = Font.class.cast(otherObj);
        return new EqualsBuilder()
            .append(this.getTypeface(), other.getTypeface())
            .append(this.getScale(), other.getScale())
            .isEquals();
    }
    
    @Override
    public int compareTo(@Nonnull Font other) {
        return new CompareToBuilder()
            .append(this.getTypeface(), other.getTypeface())
            .append(this.getScale(), other.getScale())
            .toComparison();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(this.getTypeface())
            .append(this.getScale())
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("typeface", this.getTypeface())
            .append("scale", this.getScale())
            .toString();
    }
    
    @Nonnull
    private final Typeface typeface;
    
    private final double scale;
    private static final long serialVersionUID = 1L;
}
