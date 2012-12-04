package kanga.kcae.view.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.EnumSet;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import edu.umd.cs.findbugs.annotations.SuppressWarnings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.ClosePath;
import kanga.kcae.object.Color;
import kanga.kcae.object.LineStyle;
import kanga.kcae.object.LineTo;
import kanga.kcae.object.MoveTo;
import kanga.kcae.object.Path;
import kanga.kcae.object.Pin;
import kanga.kcae.object.PinStyle;
import kanga.kcae.object.Point;
import kanga.kcae.object.ShapeGroup;
import kanga.kcae.object.SignalDirection;
import kanga.kcae.object.Symbol;

@SuppressWarnings("SIC_INNER_SHOULD_BE_STATIC_ANON")
public class SymbolEditorFrame extends JFrame {
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(SymbolEditorFrame.class);
    
    public static final ArrayList<String> dlog = new ArrayList<String>();
    
    public SymbolEditorFrame(final Symbol symbol) {
        super("Symbol Editor");
        this.symbolEditor = new SymbolEditor(symbol);
        this.pinEditorTable = new PinEditorTable(symbol);
        this.scriptFrame = new ScriptFrame();
        
        final JPanel interior = new JPanel();
        final BoxLayout interiorLayout = new BoxLayout(
            interior, BoxLayout.PAGE_AXIS);
        interior.setLayout(interiorLayout);
        interior.add(this.createToolBar());
        
        // Enclose the pin editor table in a scrolling view
        JScrollPane petScroll = new JScrollPane(
            this.pinEditorTable, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        
        final JPanel central = new JPanel();
        final BoxLayout centralLayout = new BoxLayout(
            central, BoxLayout.LINE_AXIS);
        central.setLayout(centralLayout);
        central.add(petScroll);
        central.add(this.symbolEditor);
        interior.add(central);
        
        final JSplitPane content = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
            false, interior, this.scriptFrame);
        // Resize the JSplitPane when it is initially shown.  Because the size
        // hasn't been computed yet, we can't meaningfully set the divider
        // location.
        content.addComponentListener(new ResizeSplitPane(0.8));
        
        this.getRootPane().setContentPane(content);
        this.setJMenuBar(createMenuBar());
        
        this.scriptFrame.getInterpreter().set("app", this);
        this.scriptFrame.getInterpreter().set("dlog", ResizeSplitPane.dlog);
    }
    
    private JPanel createToolBar() {
        final JPanel tb = new JPanel();
        final BoxLayout layout = new BoxLayout(tb, BoxLayout.LINE_AXIS);
        final SymbolView symView = this.symbolEditor.getViewer();
        tb.setLayout(layout);
        
        final JButton navigate = new JButton(
            new ImageIcon(Resource.getImage("NavigateTool-16x16.png")));
        final JButton line = new JButton(
            new ImageIcon(Resource.getImage("LineTool-16x16.png")));
        final JButton pin = new JButton(
            new ImageIcon(Resource.getImage("PinTool-16x16.png")));
        
        tb.add(navigate);
        tb.add(line);
        tb.add(pin);
        
        navigate.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                symView.setCurrentTool(symView.getPanZoomTool());
                symView.grabFocus();
            }
        });
        
        line.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                symView.setCurrentTool(symView.getLineTool());
                symView.grabFocus();
            }
            
        });
        
        pin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                symView.setCurrentTool(symView.getPinTool());
                symView.grabFocus();
            }
        });
        
        return tb;
    }
    
    private static JMenuBar createMenuBar() {
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
    
    public SymbolEditor getSymbolEditor() {
        return this.symbolEditor;
    }
    
    public PinEditorTable getPinEditorTable() {
        return this.pinEditorTable;
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
        sym.setLineStyle(new LineStyle(500000, Color.black));
        
        final Pin p1 = new Pin("S", "1", SignalDirection.PASSIVE,
            new Point(-10000000, 5000000), new Point(0, 5000000),
            EnumSet.of(PinStyle.BUS));
        sym.addPin(p1);
        
        app = new SymbolEditorFrame(sym);
        app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        app.setExtendedState(JFrame.MAXIMIZED_BOTH);
        app.setVisible(true);
    }

    final SymbolEditor symbolEditor;
    final PinEditorTable pinEditorTable;
    final ScriptFrame scriptFrame;
    private static final long serialVersionUID = 1L;
}