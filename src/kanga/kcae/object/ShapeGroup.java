package kanga.kcae.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.CheckForNull;

import static java.util.Collections.unmodifiableList;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class ShapeGroup implements Shape, Cloneable, Serializable {
    public ShapeGroup() {
        this.shapes = new ArrayList<Shape>();
    }

    public ShapeGroup(final Collection<Shape> shapes) {
        this.shapes = new ArrayList<Shape>(shapes);
    }
    
    @Override
    public ShapeGroup clone() throws CloneNotSupportedException {
        return (ShapeGroup) super.clone();
    }

    public List<Shape> getShapes() {
        return unmodifiableList(this.shapes);
    }
    
    protected int indexOf(final Shape shape) {
        for (int i = 0; i < this.shapes.size(); ++i) {
            if (this.shapes.get(i) == shape) {
                return i;
            }
        }
        
        return -1;
    }
    
    public boolean contains(final Shape shape) {
        return this.indexOf(shape) != -1;
    }
    
    public void addShape(final Shape shape) {
        if (shape == null) {
            throw new NullPointerException("shape cannot be null.");
        }

        if (this.contains(shape)) {
            throw new IllegalArgumentException(
                "shape " + System.identityHashCode(shape) +
                " is already present in this shape group");
        }

        this.shapes.add(shape);
    }

    public void addShapeAbove(final Shape shape, final Shape above) {
        final int aboveIndex;

        if (shape == null) {
            throw new NullPointerException("shape cannot be null.");
        }

        if (above == null) {
            throw new NullPointerException("above cannot be null.");
        }

        if (this.contains(shape)) {
            throw new IllegalArgumentException(
                "shape is already present in this sybol");
        }

        if ((aboveIndex = this.indexOf(above)) == -1) {
            throw new IllegalArgumentException(
                "above is not contained in this symbol.");
        }

        this.shapes.add(aboveIndex + 1, shape);
    }

    public void raiseShape(final Shape shape, final Shape above) {
        final int aboveIndex;
        final int shapeIndex;

        if (shape == null) {
            throw new NullPointerException("shape cannot be null.");
        }

        if (above == null) {
            throw new NullPointerException("above cannot be null.");
        }

        if ((aboveIndex = this.indexOf(above)) == -1) {
            throw new IllegalArgumentException(
                "above is not contained in this symbol.");
        }
        
        if ((shapeIndex = this.indexOf(shape)) == 1) {
            throw new IllegalArgumentException(
                "Shape " + shape + " was not present in this symbol.");
        }
        
        if (shapeIndex <= aboveIndex) {
            // Nothing to do.
            return;
        }

        this.shapes.remove(shapeIndex);
        this.shapes.add(aboveIndex + 1, shape);
        
        return;
    }

    @Override
    @CheckForNull
    public Rectangle getBoundingBox() {
        Rectangle result = null;

        for (final Shape shape : this.getShapes()) {
            final Rectangle shapeBBox = shape.getBoundingBox();

            if (result == null) { result = shapeBBox; }
            else                { result = result.union(shapeBBox); }
        }

        return result;
    }

    @Override
    public void setLineStyle(final LineStyle lineStyle) {
        for (final Shape shape : this.shapes) {
            shape.setLineStyle(lineStyle);
        }
    }

    @Override
    public void setFillStyle(final FillStyle fillStyle) {
        for (final Shape shape : this.shapes) {
            shape.setFillStyle(fillStyle);
        }
    }
    
    @Override
    public ShapeGroup scale(double factor) {
        ShapeGroup newSG = new ShapeGroup();
        for (Shape shape : this.getShapes()) {
            newSG.addShape(shape.scale(factor));
        }
        
        return newSG;
    }

    @Override
    public ShapeGroup translate(long dx, long dy) {
        ShapeGroup newSG = new ShapeGroup();
        for (Shape shape : this.getShapes()) {
            newSG.addShape(shape.translate(dx, dy));
        }
        
        return newSG;
    }
    
    @Override
    public ShapeGroup rotateQuadrant(int nQuadrants) {
        ShapeGroup newSG = new ShapeGroup();
        for (Shape shape : this.getShapes()) {
            newSG.addShape(shape.rotateQuadrant(nQuadrants));
        }
        
        return newSG;
    }


    @Override
    public boolean equals(final Object otherObj) {
        if (otherObj == null) { return false; }
        if (otherObj == this) { return true; }
        if (otherObj.getClass() != this.getClass()) { return false; }
        
        final ShapeGroup other = (ShapeGroup) otherObj;
        return this.getShapes().equals(other.getShapes());
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(5211, 34111)
            .append(this.getShapes())
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, SHORT_PREFIX_STYLE)
            .append("shapes", this.getShapes())
            .toString();
    }
    
    private final List<Shape> shapes;
    private static final long serialVersionUID = 1L;
}
