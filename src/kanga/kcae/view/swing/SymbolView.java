package kanga.kcae.view.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

import static java.lang.Math.round;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.BaseUnit;
import kanga.kcae.object.Pin;
import kanga.kcae.object.Point;
import kanga.kcae.object.Rectangle;
import kanga.kcae.object.Symbol;
import static java.awt.Color.WHITE;

public class SymbolView extends MeasuredViewPanel {
    static final Log log = LogFactory.getLog(SymbolView.class);

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
             (symbol != null ? symbol.getBoundingBox() : null), baseUnit);
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
        this.lineTool = new LineTool(this);
        this.pinTool = new PinTool(this);
        this.setPreferredSize(new Dimension(1000, 600));
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        super.paintComponent(graphics);
        final Graphics2D g = (Graphics2D) graphics;
        final Symbol symbol = this.getSymbol();
        final MeasuredViewTool tool = this.getCurrentTool();
        
        if (tool != null) {
            tool.paintBackground(g);
        }

        if (symbol == null) {
            log.debug("paintComponent: symbol is null (nothing to paint)");
        }
        else if (this.getViewArea() == null) {
            log.debug("paintComponent: viewArea is null");
        }
        else {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
            ShapePainter.paint(g, symbol.getShapes());
            for (final Pin pin : symbol.getPins()) {
                ShapePainter.paint(g, pin);
            }
        }

        if (tool != null) {
            tool.paintOverlay(g);
        }
    }
    
    public Symbol getSymbol() {
        return this.symbol;
    }
    
    public void setSymbol(Symbol symbol) {
        this.symbol = symbol;
    }
    
    public LineTool getLineTool() {
        return this.lineTool;
    }
    
    public PinTool getPinTool() {
        return this.pinTool;
    }
    
    public Point roundToGrid(Point p) {
        long gridSpacing = this.getGridSpacing();
        double fGridSpacing = (double) gridSpacing;
        return new Point(gridSpacing * round(p.getX() / fGridSpacing),
                         gridSpacing * round(p.getY() / fGridSpacing));
    }
    
    public long getGridSpacing() {
        return this.gridSpacing;
    }
    
    public void setGridSpacing(final long gridSpacing) {
        this.gridSpacing = gridSpacing;
    }
    
    private Symbol symbol;
    private final LineTool lineTool;
    private final PinTool pinTool;
    private long gridSpacing = 1000000; // 1 mm
    private static final long serialVersionUID = 1L;
}