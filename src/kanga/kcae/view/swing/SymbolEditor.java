package kanga.kcae.view.swing;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import kanga.kcae.object.Symbol;
import static java.awt.Color.WHITE;

public class SymbolEditor extends JPanel {
	private static final long serialVersionUID = -6398202983953748987L;
	public SymbolEditor() {
        this(null, null, true);
    }

    public SymbolEditor(final Symbol symbol) {
        this(symbol, null, true);
    }

    public SymbolEditor(
        final Symbol symbol,
        final LayoutManager layoutManager)
    {
        this(symbol, layoutManager, true);
    }

    public SymbolEditor(
        final Symbol symbol,
        final boolean isDoubleBuffered)
    {
        this(symbol, null, isDoubleBuffered);
    }

    public SymbolEditor(
        final Symbol symbol,
        final LayoutManager layoutManager,
        final boolean isDoubleBuffered)
    {
        super(layoutManager, isDoubleBuffered);
        this.symbol = symbol;
        this.backgroundColor = WHITE;
    }

    @Override
    protected void paintComponent(final Graphics graphics) {
        final Graphics2D g = (Graphics2D) graphics;
        Rectangle clip = g.getClipBounds();

        if (clip == null) {
            clip = this.getBounds();
        }

        g.setColor(this.backgroundColor);
        g.fillRect(clip.x, clip.y, clip.width, clip.height);

        if (this.symbol == null)
            return;
    }

    public static void main(String[] args) {
        final JFrame frame = new JFrame("Symbol Editor");
        final JRootPane root = frame.getRootPane();
        final Container content = root.getContentPane();

        content.add(new SymbolEditor());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private Symbol symbol;
    private Color backgroundColor;
}