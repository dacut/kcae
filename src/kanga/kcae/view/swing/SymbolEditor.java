package kanga.kcae.view.swing;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import static java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment;

import kanga.kcae.object.BaseUnit;
import kanga.kcae.object.Symbol;

public class SymbolEditor extends MeasuredViewer<SymbolView> {
    private static final long serialVersionUID = -6398202983953748987L;
    
    public SymbolEditor() {
        this(null);
    }
    
    public SymbolEditor(final Symbol symbol) {
        this(symbol, BaseUnit.meter);
    }

    public SymbolEditor(final Symbol symbol, final BaseUnit baseUnit) {
        super(new SymbolView(symbol), baseUnit);
        final GraphicsEnvironment gfxEnv = getLocalGraphicsEnvironment();
        final GraphicsDevice gfxDev = gfxEnv.getDefaultScreenDevice();
        final DisplayMode dm = gfxDev.getDisplayMode();
        final Dimension mySize = new Dimension(
            dm.getWidth(), (int) (dm.getHeight() * 0.7));
        this.setPreferredSize(mySize);
    }
}