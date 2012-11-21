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
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(Font.class);
    
    public Font(
        @Nonnull final Typeface typeface,
        final int capitalHeight)
    {
        this(typeface, capitalHeight, null);
    }
    
    public Font(
        @Nonnull final Typeface typeface,
        final long capitalHeight,
        final int strokeWidth)
    {
        this(typeface, capitalHeight, new LineStyle(strokeWidth));
    }
    
    public Font(
        @Nonnull final Typeface typeface,
        final long capitalHeight,
        @CheckForNull final LineStyle strokeStyle)
    {
        if (typeface == null) {
            throw new NullPointerException("typeface cannot be null");
        }
        
        this.typeface = typeface;
        this.scale = ((double) capitalHeight) /
                     ((double) typeface.getCapitalHeight());
        this.strokeStyle = strokeStyle;
        return;
    }
    
    @Nonnull
    public Shape render(@CheckForNull CharSequence text) {
        final ShapeGroup result = new ShapeGroup();
        final LineStyle strokeStyleOverride = this.getStrokeStyle();
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
            
            if (strokeStyleOverride != null) {
                glyphPath.setLineStyle(strokeStyleOverride);
            }
            
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
    
    @CheckForNull
    public LineStyle getStrokeStyle() {
        return this.strokeStyle;
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
            .append(this.getStrokeStyle(), other.getStrokeStyle())
            .isEquals();
    }
    
    @Override
    public int compareTo(@Nonnull Font other) {
        return new CompareToBuilder()
            .append(this.getTypeface(), other.getTypeface())
            .append(this.getScale(), other.getScale())
            .append(this.getStrokeStyle(), other.getStrokeStyle())
            .toComparison();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(this.getTypeface())
            .append(this.getScale())
            .append(this.getStrokeStyle())
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("typeface", this.getTypeface())
            .append("scale", this.getScale())
            .append("strokeStyle", this.getStrokeStyle())
            .toString();
    }
    
    @Nonnull
    private final Typeface typeface;
    private final double scale;
    @CheckForNull
    private final LineStyle strokeStyle;
    private static final long serialVersionUID = 1L;
}
