package kanga.kcae.view.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;

import kanga.kcae.object.BaseUnit;
import kanga.kcae.object.Rectangle;
import kanga.kcae.object.Symbol;
import static java.awt.Color.WHITE;

public class SymbolView extends MeasuredViewPanel {
    private static final long serialVersionUID = 6237440111082245444L;
    public SymbolView() {
        this(null, null, true, BaseUnit.meter);
    }

    public SymbolView(final Symbol symbol) {
        this(symbol, null, true, BaseUnit.meter);
    }

    public SymbolView(
        final Symbol symbol,
        final BaseUnit baseUnit)
    {
        this(symbol, null, true, baseUnit);
    }
    
    public SymbolView(
        final Symbol symbol,
        final LayoutManager layoutManager,
        final boolean isDoubleBuffered,
        final BaseUnit baseUnit)
    {
        this(symbol, layoutManager, isDoubleBuffered,
             (symbol != null ? symbol.getBoundingBox() :
                               Rectangle.fromPoints(0, 0, 1, 1)),
             baseUnit);
    }

    public SymbolView(
        final Symbol symbol,
        final LayoutManager layoutManager,
        final boolean isDoubleBuffered,
        final Rectangle viewArea,
        final BaseUnit baseUnit)
    {
        super(layoutManager, isDoubleBuffered, viewArea, baseUnit);
        this.symbol = symbol;
        this.backgroundColor = WHITE;
        this.setPreferredSize(new Dimension(1000, 600));
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        final Graphics2D g = (Graphics2D) graphics;
        final Symbol symbol = this.getSymbol();

        if (symbol == null)
            return;
        
        ShapePainter.paint(g, symbol.getShapes());
    }
    
    public Symbol getSymbol() {
        return this.symbol;
    }
    
    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }
    
    private Symbol symbol;
}