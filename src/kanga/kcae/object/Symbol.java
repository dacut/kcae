package kanga.kcae;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

public class Symbol implements Comparable<Symbol> {
    public Symbol(final String name) {
        this.name(name);
        return;
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

        this.shapes.add(aboveIndex, shape);
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

        this.shapes.add(aboveIndex, shape);
        return;
    }

    private String name;
    private final List<Shape> shapes = new ArrayList<Shape>();
    private final Map<Port> portsByName = new HashMap<String, Port>();
}