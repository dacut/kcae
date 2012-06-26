package kanga.kcae.view.swing;

import static javax.swing.SwingUtilities.invokeLater;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.ScriptInterpreter;

public class ScriptFrame extends JSplitPane implements DocumentListener {
    protected static final Log log = LogFactory.getLog(ScriptFrame.class);
    
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
        super(JSplitPane.VERTICAL_SPLIT, false,
              new JScrollPane(
                  history, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED),
              new JScrollPane(
                  currentEntry, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                  JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED));
        this.interp = interp;
        this.history = history;
        this.currentEntry = currentEntry;       
        this.currentEntry.setBackground(Color.YELLOW);
        this.currentEntry.setRows(5);
        this.currentEntry.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 10));
        this.currentEntry.getDocument().addDocumentListener(this);
        return;
    }
    
    public boolean interpret(String text) {
        return this.interp.runsource(text);
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

    @SuppressWarnings("unused")
    private final ScriptInterpreterHistory history;
    private final ScriptInterpreter interp;
    private final JTextArea currentEntry;
    private static final long serialVersionUID = 1L;
}
