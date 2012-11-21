package kanga.kcae.view.swing;

import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.python.core.PyObject;

import kanga.kcae.object.ScriptInterpreter;

@SuppressWarnings("unused")
public class ScriptFrame extends JSplitPane implements DocumentListener {
    protected static final Log log = LogFactory.getLog(ScriptFrame.class);
    public static final int DEBUGGER_MARGIN = 2;
    
    public ScriptFrame() {
        this(new ScriptInterpreter());
    }
    
    public ScriptFrame(final ScriptInterpreter interp) {
        this(interp, new ScriptInterpreterHistory(interp), new JTextArea());
    }
    
    private static class ClearCurrentEntry implements Runnable {
        ClearCurrentEntry(final Document doc) {
            this.doc = doc;
        }
            
        @Override
        public void run() {
            try {
                this.doc.remove(0,  this.doc.getLength());
            }
            catch (BadLocationException e) {
                log.error("Failed to clear currentEntry", e);
            }
            
        }
        
        private final Document doc;
    }
    
    private ScriptFrame(
        final ScriptInterpreter interp,
        final ScriptInterpreterHistory history,
        final JTextArea currentEntry)
    {
        this(interp, history, currentEntry,
             new JScrollPane(
                 history, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
             new JScrollPane(
                 currentEntry, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                 JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
    }
    
    private ScriptFrame(
        final ScriptInterpreter interp,
        final ScriptInterpreterHistory history,
        final JTextArea currentEntry,
        final JScrollPane historyScroll,
        final JScrollPane entryScroll)
    {
        super(JSplitPane.VERTICAL_SPLIT, false, historyScroll, entryScroll);
        this.interp = interp;
        this.history = history;
        this.currentEntry = currentEntry;
        this.historyScroll = historyScroll;
        this.entryScroll = entryScroll;
        
        this.currentEntry.setBackground(Color.YELLOW);
        // this.currentEntry.setRows(5);
        this.currentEntry.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        this.currentEntry.getDocument().addDocumentListener(this);
        return;
    }
    
    public boolean interpret(String text) {
        if (text.startsWith("debug ")) {
            this.debug(text.substring(6).trim());
            return false;
        }
        
        return this.interp.runsource(text);
    }
    
    Frame getFrame() {
        Container container = this.getParent();
        while (! (container instanceof Frame))
            container = container.getParent();
        
        return (Frame) container;
    }
    
    public void debug(String expression) {
        PyObject target = this.interp.eval(expression);
        ObjectDebugFrame debugger = new ObjectDebugFrame(expression, target);
        debugger.setVisible(true);
        
        // If our frame invades the bounds of the debugger, shrink the frame
        // so there's no overlap.
        final Rectangle debuggerBounds = debugger.getBounds();
        final Frame frame = getFrame();
        final Rectangle frameBounds = frame.getBounds();
        final int rightLimit = debuggerBounds.x - DEBUGGER_MARGIN;
        
        if ((frameBounds.x < debuggerBounds.x + debuggerBounds.width) &&
            (frameBounds.x + frameBounds.width > rightLimit))
        {
            // Overlapping on the right edge.  Shrink it.
            frame.setBounds(new Rectangle(
                    frameBounds.x, frameBounds.y,
                    rightLimit - frameBounds.x, frameBounds.height));
        }
    }
   
    @Override
    public void changedUpdate(final DocumentEvent e) {
        return;
    }
    
    @Override
    public void insertUpdate(final DocumentEvent e) {
        final Document doc = e.getDocument();
        final int length = e.getLength();
        final int docLength = doc.getLength();
        final int offset = e.getOffset();
        
        if (offset != docLength - 1) {
            return;
        }
                
        try {
            final String text = doc.getText(offset, length);
            if (! text.equals("\n")) {
                return;
            }

            final String entry = doc.getText(0, docLength);
            if (! this.interpret(entry)) {
                // Successful interpretation; clear out the text.
                invokeLater(new ClearCurrentEntry(doc));
            }
        }
        catch (final BadLocationException ex) {
            System.err.println(ex);
        }
    }
    
    @Override
    public void removeUpdate(final DocumentEvent e) {
        return;
    }
    
    public ScriptInterpreter getInterpreter() {
        return this.interp;
    }
    
    public static void main(String[] args) {
        JFrame mainWindow = new JFrame("Script frame");
        ScriptFrame sf = new ScriptFrame();
        mainWindow.add(sf);
        mainWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainWindow.setVisible(true);
        mainWindow.setSize(1000, 800);
        sf.setDividerLocation(400);
    }
    
    private final ScriptInterpreter interp;
    private final ScriptInterpreterHistory history;
    private final JTextArea currentEntry;
    private final JScrollPane historyScroll;
    private final JScrollPane entryScroll;
    private static final long serialVersionUID = 1L;
}
