package kanga.kcae.object;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.io.IOUtils.closeQuietly;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Typeface implements Comparable<Typeface>, Serializable {
    public static final String resourcePackage = "kanga/kcae/res/";
    
    public Typeface(String name, int descenderHeight, int medianHeight,
                     int capitalHeight, int ascenderHeight, int xHeight,
                     Map<Character, Glyph> glyphs, Glyph replacementGlyph)
    {
        this.name = name;
        this.descenderHeight = descenderHeight;
        this.medianHeight = medianHeight;
        this.capitalHeight = capitalHeight;
        this.ascenderHeight = ascenderHeight;
        this.xHeight = xHeight;
        this.glyphs = new HashMap<Character, Glyph>(glyphs);
        this.replacementGlyph = replacementGlyph;
        
        return;
    }
    
    public String getName() {
        return this.name;
    }
    
    public int getDescenderHeight() {
        return this.descenderHeight;
    }

    public int getMedianHeight() {
        return this.medianHeight;
    }

    public int getCapitalHeight() {
        return this.capitalHeight;
    }

    public int getAscenderHeight() {
        return this.ascenderHeight;
    }

    public int getXHeight() {
        return this.xHeight;
    }
    
    public Glyph getGlyph(char c) {
        Glyph result = this.glyphs.get(c);
        if (result == null)
            result = this.getReplacementGlyph();
        
        return result;
    }

    public Glyph getReplacementGlyph() {
        return this.replacementGlyph;
    }
    
    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null)                       { return false; }
        if (this == otherObj)                       { return true; }
        if (this.getClass() != otherObj.getClass()) { return false; }
        
        Typeface other = Typeface.class.cast(otherObj);
        
        return new EqualsBuilder()
            .append(this.getName(), other.getName())
            .append(this.getAscenderHeight(), other.getAscenderHeight())
            .append(this.getCapitalHeight(), other.getCapitalHeight())
            .append(this.getDescenderHeight(), other.getDescenderHeight())
            .append(this.getMedianHeight(), other.getMedianHeight())
            .append(this.getXHeight(), other.getXHeight())
            .append(this.getReplacementGlyph(), other.getReplacementGlyph())
            .append(this.glyphs, other.glyphs)
            .isEquals();
    }
    
    @Override
    public int compareTo(Typeface other) {
        return new CompareToBuilder()
            .append(this.getName(), other.getName())
            .append(this.getAscenderHeight(), other.getAscenderHeight())
            .append(this.getCapitalHeight(), other.getCapitalHeight())
            .append(this.getDescenderHeight(), other.getDescenderHeight())
            .append(this.getMedianHeight(), other.getMedianHeight())
            .append(this.getXHeight(), other.getXHeight())
            .append(this.getReplacementGlyph(), other.getReplacementGlyph())
            .append(this.glyphs, other.glyphs)
            .toComparison();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(this.getName())
            .append(this.getAscenderHeight())
            .append(this.getCapitalHeight())
            .append(this.getDescenderHeight())
            .append(this.getMedianHeight())
            .append(this.getXHeight())
            .append(this.getReplacementGlyph())
            .append(this.glyphs)
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("name", this.getName())
            .append("ascenderHeight", this.getAscenderHeight())
            .append("capitalHeight", this.getCapitalHeight())
            .append("descenderHeight", this.getDescenderHeight())
            .append("medianHeight", this.getMedianHeight())
            .append("xHeight", this.getXHeight())
            .append("replacementGlyph", this.getReplacementGlyph())
            .append("glyphs", this.glyphs)
            .toString();
    }
    
    public static Typeface find(String name) {
        InputStream typefaceStream = ClassLoader.getSystemResourceAsStream(
            resourcePackage + name + ".fnt");
        if (typefaceStream == null) {
            throw new UnknownTypefaceException("Unknown typeface " + name);
        }
        
        ObjectInputStream ois;
        
        try {
            ois = new ObjectInputStream(typefaceStream);
        }
        catch (IOException e) {
            closeQuietly(typefaceStream);
            throw new UnknownTypefaceException("Unknown typeface " + name, e);
        }
        
        try {
            return (Typeface) ois.readObject();
        }
        catch (Exception e) {
            throw new UnknownTypefaceException("Unknown typeface " + name, e);
        }
        finally {
            closeQuietly(ois);
        }
    }
    
    private final String name;
    private final int descenderHeight;
    private final int medianHeight;
    private final int capitalHeight;
    private final int ascenderHeight;
    private final int xHeight;
    private final Map<Character, Glyph> glyphs;
    private final Glyph replacementGlyph;
    
    private static final long serialVersionUID = 1L;
}
