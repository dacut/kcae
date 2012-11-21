package kanga.kcae.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static java.util.Collections.unmodifiableList;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class Path implements Shape, Comparable<Path>, Serializable {
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(Path.class);
    
    public Path() {
        this.instructions = new ArrayList<PathInstruction>();
        this.lineStyle = null;
        this.fillStyle = null;
    }
    
    public Path(
        @CheckForNull Collection<PathInstruction> pathInstructions,
        @Nullable LineStyle lineStyle,
        @Nullable FillStyle fillStyle)
    {
        this.instructions =
            (pathInstructions != null ?
             new ArrayList<PathInstruction>(pathInstructions) :
             new ArrayList<PathInstruction>());
        
        this.lineStyle = lineStyle;
        this.fillStyle = fillStyle;
    }
    
    public Path(
        @CheckForNull PathInstruction[] pathInstructions,
        @Nullable LineStyle lineStyle,
        @Nullable FillStyle fillStyle)
    {
        this.instructions =
            (pathInstructions != null ?
             new ArrayList<PathInstruction>(pathInstructions.length) :
             new ArrayList<PathInstruction>());
        this.lineStyle = lineStyle;
        this.fillStyle = fillStyle;        
        
        if (pathInstructions != null) {
            for (PathInstruction pi : pathInstructions) {
                this.instructions.add(pi);
            }
        }
    }
    
    @Nonnull
    public List<PathInstruction> getInstructions() {
        return unmodifiableList(this.instructions);
    }

    public void addInstruction(@Nonnull PathInstruction instruction) {
        if (instruction == null) {
            throw new NullPointerException("instruction cannot be null");
        }

        this.instructions.add(instruction);
    }
    
    public void addPath(@Nonnull Path path) {
        if (path == null) {
            throw new NullPointerException("path cannot be null");
        }
        
        for (PathInstruction pi : path.getInstructions()) {
            this.addInstruction(pi);
        }
    }

    public boolean removeInstruction(@Nonnull PathInstruction instruction) {
        if (instruction == null) {
            throw new NullPointerException("instruction cannot be null");
        }

        return this.instructions.remove(instruction);
    }

    public int size() {
        return this.instructions.size();
    }

    @Override
    @Nullable
    public Rectangle getBoundingBox() {
        Point pos = null;
        Rectangle result = null;

        for (PathInstruction inst : this.instructions) {
            result = inst.updateBoundingBox(pos, result);
            pos = inst.updatePosition(pos);
        }

        return result;
    }

    @Nullable
    public LineStyle getLineStyle() {
        return this.lineStyle;
    }

    @Override
    public void setLineStyle(@Nullable LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    @Nullable
    public FillStyle getFillStyle() {
        return this.fillStyle;
    }

    @Override
    public void setFillStyle(@Nullable FillStyle fillStyle) {
        this.fillStyle = fillStyle;
    }
    
    @Override
    @Nonnull
    public Path scale(double factor) {
        Path newPath = new Path();
        newPath.setFillStyle(this.getFillStyle());
        newPath.setLineStyle(this.getLineStyle());
        
        for (PathInstruction pi : this.getInstructions()) {
            newPath.addInstruction(pi.scale(factor));
        }
        
        return newPath;
    }

    @Override
    @Nonnull
    public Path translate(long dx, long dy) {
        Path newPath = new Path();
        newPath.setFillStyle(this.getFillStyle());
        newPath.setLineStyle(this.getLineStyle());
        
        for (PathInstruction pi : this.getInstructions()) {
            newPath.addInstruction(pi.translate(dx, dy));
        }
        
        return newPath;
    }

    @Override
    @Nonnull
    public Path rotateQuadrant(int nQuadrants) {
        Path newPath = new Path();
        newPath.setFillStyle(this.getFillStyle());
        newPath.setLineStyle(this.getLineStyle());
        
        for (PathInstruction pi : this.getInstructions()) {
            newPath.addInstruction(pi.rotateQuadrant(nQuadrants));
        }
        
        return newPath;
    }

    @Override
    public boolean equals(@Nullable Object otherObj) {
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
    public int compareTo(@Nonnull Path other) {
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

    @Nullable
    private LineStyle lineStyle;
    
    @Nullable
    private FillStyle fillStyle;
    
    @Nonnull
    private List<PathInstruction> instructions;
    
    private static final long serialVersionUID = 1L;
}