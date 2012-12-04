package kanga.kcae.xchg.autocad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.unmodifiableList;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import kanga.kcae.object.Glyph;
import kanga.kcae.object.LineStyle;

public class AutoCADShape implements Serializable {
    public AutoCADShape(String shapeName, List<ShapeInstruction> instructions) {
        this(shapeName, instructions, false);
    }
    
    AutoCADShape(
        String shapeName,
        List<ShapeInstruction> instructions,
        boolean takeOwnership)
    {
        this.shapeName = shapeName;
        this.instructions = (takeOwnership ?
                             instructions :
                             new ArrayList<ShapeInstruction>(instructions));
    }
    
    public String getShapeName() {
        return this.shapeName;
    }
    
    public List<ShapeInstruction> getInstructions() {
        return unmodifiableList(this.instructions);
    }
    
    public Glyph toGlyph(
        final Map<Character, AutoCADShape> shapes,
        final LineStyle lineStyle,
        final ShapeToGlyph stg)
    {
        for (ShapeInstruction si : this.getInstructions()) {
            si.visit(stg);
        }
        
        return stg.getGlyph();
    }
    
    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (this == otherObj) { return true; }
        if (this.getClass() != otherObj.getClass()) { return false; }
        
        AutoCADShape other = AutoCADShape.class.cast(otherObj);
        
        return new EqualsBuilder()
            .append(this.getShapeName(), other.getShapeName())
            .append(this.getInstructions(), other.getInstructions())
            .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(998513353, 1108739267)
            .append(this.getShapeName())
            .append(this.getInstructions())
            .toHashCode();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("shapeName", this.getShapeName())
            .append("instructions", this.getInstructions())
            .toString();
    }
    
    private final String shapeName;
    private final List<ShapeInstruction> instructions;
    private static final long serialVersionUID = 1L;
}
