package kanga.kcae.view.swing;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;

import kanga.kcae.object.BaseUnit;
import kanga.kcae.object.Rectangle;
import kanga.kcae.object.Symbol;
import static kanga.kcae.object.PaperDimension.MinA4AnsiA;

public class SymbolEditor extends MeasuredViewDecorator<SymbolView> {
    public static final Rectangle defaultViewArea =
        Rectangle.atOrigin(MinA4AnsiA.landscape);
    
    public SymbolEditor() {
        this(null);
    }
    
    public SymbolEditor(final Symbol symbol) {
        this(symbol, BaseUnit.meter);
    }

    public SymbolEditor(final Symbol symbol, final BaseUnit baseUnit) {
        super(new SymbolView(symbol), getInitialViewArea(symbol), baseUnit);
        final GraphicsEnvironment gfxEnv = getLocalGraphicsEnvironment();
        final GraphicsDevice gfxDev = gfxEnv.getDefaultScreenDevice();
        final DisplayMode dm = gfxDev.getDisplayMode();
        final Dimension mySize = new Dimension(
            dm.getWidth(), (int) (dm.getHeight() * 0.7));
        this.setPreferredSize(mySize);
    }
    
    private static Rectangle getInitialViewArea(final Symbol symbol) {
        if (symbol == null)
            return defaultViewArea;
        
        final Rectangle result = symbol.getBoundingBox();
        if (result == null)
            return defaultViewArea;
        return result;
    }
    
    private static final long serialVersionUID = 1L;
}