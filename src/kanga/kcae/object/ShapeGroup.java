package kanga.kcae.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import static java.util.Collections.unmodifiableList;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import static org.apache.commons.lang3.builder.ToStringStyle.SHORT_PREFIX_STYLE;

public class ShapeGroup implements Shape {
    public ShapeGroup() {
        this.shapes = new ArrayList<Shape>();
    }

    public ShapeGroup(final Collection<Shape> shapes) {
        this.shapes = new ArrayList<Shape>(shapes);
    }

    public List<Shape> getShapes() {
        return unmodifiableList(this.shapes);
    }

    public void addShape(final Shape shape) {
        if (shape == null) {
            throw new NullPointerException("shape cannot be null.");
        }

        if (this.shapes.contains(shape)) {
            throw new IllegalArgumentException(
                "shape is already present in this sybol");
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

        if (this.shapes.contains(shape)) {
            throw new IllegalArgumentException(
                "shape is already present in this sybol");
        }

        if ((aboveIndex = this.shapes.indexOf(above)) == -1) {
            throw new IllegalArgumentException(
                "above is not contained in this symbol.");
        }

        this.shapes.add(aboveIndex + 1, shape);
    }

    public void raiseShape(final Shape shape, final Shape above) {
        final int aboveIndex;

        if (shape == null) {
            throw new NullPointerException("shape cannot be null.");
        }

        if (above == null) {
            throw new NullPointerException("above cannot be null.");
        }

        if ((aboveIndex = this.shapes.indexOf(above)) == -1) {
            throw new IllegalArgumentException(
                "above is not contained in this symbol.");
        }

        if (! this.shapes.remove(shape)) {
            throw new IllegalArgumentException(
                "Shape " + shape + " was not present in this symbol.");
        }

        this.shapes.add(aboveIndex + 1, shape);
        return;
    }

    @Override
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
}
