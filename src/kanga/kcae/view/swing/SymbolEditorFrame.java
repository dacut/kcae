package kanga.kcae.view.swing;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JSplitPane;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.ClosePath;
import kanga.kcae.object.LineTo;
import kanga.kcae.object.MoveTo;
import kanga.kcae.object.Path;
import kanga.kcae.object.ShapeGroup;
import kanga.kcae.object.Symbol;

public class SymbolEditorFrame extends JFrame {
    private static final long serialVersionUID = -4078447997380910996L;
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(SymbolEditorFrame.class);
    
    public static ArrayList<String> dlog = new ArrayList<String>();
    
    public SymbolEditorFrame() {
        this(null);
    }

    public SymbolEditorFrame(final Symbol symbol) {
        super("Symbol Editor");
        this.symbolEditor = new SymbolEditor(symbol);
        this.scriptFrame = new ScriptFrame();
        
        final JSplitPane content = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            false, this.symbolEditor, this.scriptFrame);
        // Resize the JSplitPane when it is initially shown.  Because the size
        // hasn't been computed yet, we can't meaningfully set the divider
        // location.
        content.addComponentListener(new ResizeSplitPane(0.8));
        
        this.getRootPane().setContentPane(content);
        this.setJMenuBar(this.createMenuBar());
        
        this.scriptFrame.getInterpreter().set("app", this);
        this.scriptFrame.getInterpreter().set("dlog", ResizeSplitPane.dlog);
    }

    private JMenuBar createMenuBar() {
        final JMenuBar mb = new JMenuBar();
        final JMenu mFile = new JMenu("File");
        final JMenu mTools = new JMenu("Tools");
        final JMenuItem miFileNew = new JMenuItem("New");
        final JMenuItem miFileSave = new JMenuItem("Save");
        final JMenuItem miFileSaveAs = new JMenuItem("Save As...");
        final JMenuItem miFileClose = new JMenuItem("Close");
        final JMenuItem miToolsSelect = new JMenuItem("Select");
        final JMenuItem miToolsLine = new JMenuItem("Line");

        mb.add(mFile);
        mb.add(mTools);

        mFile.add(miFileNew);
        mFile.add(miFileSave);
        mFile.add(miFileSaveAs);
        mFile.add(miFileClose);

        mTools.add(miToolsSelect);
        mTools.add(miToolsLine);

        mb.setVisible(true);

        return mb;
    }
    
    public static void main(String[] args) {
        final SymbolEditorFrame app;
        final ShapeGroup sg = new ShapeGroup();
        final Symbol sym = new Symbol("opamp");
        final Path p = new Path();
        
        p.addInstruction(new MoveTo(0, 0));
        p.addInstruction(new LineTo(50000000, 50000000));
        p.addInstruction(new LineTo(0, 100000000));
        p.addInstruction(new ClosePath());
        sg.addShape(p);
        sym.setShapes(sg);
        
        app = new SymbolEditorFrame(sym);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setVisible(true);
    }

    final SymbolEditor symbolEditor;
    final ScriptFrame scriptFrame;
}