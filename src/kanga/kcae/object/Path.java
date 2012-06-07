package kanga.kcae.object;

import java.util.ArrayList;
import java.util.List;
import static java.util.Collections.unmodifiableList;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class Path implements Shape, Comparable<Path> {
    public Path() {
        this.instructions = new ArrayList<PathInstruction>();
        this.lineStyle = null;
        this.fillStyle = null;
    }

    public List<PathInstruction> getInstructions() {
        return unmodifiableList(this.instructions);
    }

    public void addInstruction(final PathInstruction instruction) {
        if (instruction == null) {
            throw new NullPointerException("instruction cannot be null.");
        }

        this.instructions.add(instruction);
    }

    public boolean removeInstruction(final PathInstruction instruction) {
        if (instruction == null) {
            throw new NullPointerException("instruction cannot be null.");
        }

        return this.instructions.remove(instruction);
    }

    public int size() {
        return this.instructions.size();
    }

    /**
     * FIXME
     */
    @Override
    public Rectangle getBoundingBox() {
        Rectangle result = null;
        return result;
    }

    public LineStyle getLineStyle() {
        return this.lineStyle;
    }

    @Override
    public void setLineStyle(final LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    public FillStyle getFillStyle() {
        return this.fillStyle;
    }

    @Override
    public void setFillStyle(final FillStyle fillStyle) {
        this.fillStyle = fillStyle;
    }

    @Override
    public boolean equals(Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }
        
        final Path other = (Path) otherObj;
        return new EqualsBuilder()
            .append(this.getLineStyle(), other.getLineStyle())
            .append(this.getFillStyle(), other.getFillStyle())
            .append(this.getInstructions(), other.getInstructions())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(49817, 43297)
            .append(this.getLineStyle())
            .append(this.getFillStyle())
            .append(this.getInstructions())
            .toHashCode();
    }

    @Override
    public int compareTo(Path other) {
        return new CompareToBuilder()
            .append(this.getLineStyle(), other.getLineStyle())
            .append(this.getFillStyle(), other.getFillStyle())
            .append(this.getInstructions(), other.getInstructions())
            .toComparison();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("lineStyle", this.getLineStyle())
            .append("fillStyle", this.getFillStyle())
            .append("points", this.getInstructions())
            .toString();
    }

    private LineStyle lineStyle;
    private FillStyle fillStyle;
    private List<PathInstruction> instructions;
}