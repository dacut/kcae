package kanga.kcae.view.swing;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import kanga.kcae.object.ScriptCompilationListener;
import kanga.kcae.object.ScriptInterpreter;

import org.python.core.PyException;
import org.python.core.PyFile;
import org.python.core.PySystemState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ScriptInterpreterHistory
    extends JTextPane
    implements ScriptCompilationListener
{
    private static final Log log = LogFactory.getLog(
        ScriptInterpreterHistory.class);
    private static final long serialVersionUID = 1L;
    
    private class InterpreterOutputHandler extends OutputStream {
        private final AttributeSet attributeSet;

        InterpreterOutputHandler(final AttributeSet attributeSet) {
            this.attributeSet = attributeSet;
        }
        
        @Override
        public void write(int b) throws IOException {
            Document doc = ScriptInterpreterHistory.this.getDocument();
            try {
                doc.insertString(doc.getLength(), String.valueOf((char) b),
                                 this.attributeSet);
                ScriptInterpreterHistory.this.setCaretPosition(doc.getLength());
            }
            catch (final BadLocationException e) {
                throw new IOException(e);
            }
        }
    }

    public ScriptInterpreterHistory(final ScriptInterpreter interp) {
        super();
        this.setEditable(false);
        interp.addCompilationListener(this);
        
        this.baseStyle = this.addStyle("base", null);
        this.stdinStyle = this.addStyle("stdin", this.baseStyle);
        this.stdoutStyle = this.addStyle("stdout", this.baseStyle);
        this.stderrStyle = this.addStyle("stderr", this.baseStyle);
        StyleConstants.setFontFamily(this.baseStyle, Font.MONOSPACED);
        StyleConstants.setFontSize(this.baseStyle, 10);
        StyleConstants.setForeground(this.stdinStyle, Color.GREEN);
        StyleConstants.setForeground(this.stdoutStyle, Color.BLACK);
        StyleConstants.setForeground(this.stderrStyle, Color.RED);
        StyleConstants.setBold(this.stderrStyle, true);
        
        this.stdoutHandler = new InterpreterOutputHandler(this.stdoutStyle);
        this.stderrHandler = new InterpreterOutputHandler(this.stderrStyle);
        this.commandHistory = new ArrayList<String>(2500);
        
        final PySystemState ss = interp.getSystemState();
        ss.stdout = ss.__stdout__ = new PyFile(this.stdoutHandler, 0);
        ss.stderr = ss.__stderr__ = new PyFile(this.stderrHandler, 0);
    }
    
    public void scrollToEnd() {
        
    }
    
    public void writeStdin(final CharSequence str) {
        this.write(str, this.stdinStyle);
    }
    
    public void writeStdout(final CharSequence str) {
        this.write(str, this.stdoutStyle);
    }

    public void writeStderr(final CharSequence str) {
        this.write(str, this.stderrStyle);
    }
    
    public void write(final CharSequence str, final Style style) {
        final Document doc = this.getDocument();
        try {
            doc.insertString(doc.getLength(), str.toString(), style);
        } catch (final BadLocationException e) {
            log.error("Failed to insert " + style.getName() + " text", e);
        }
    }

    @Override
    public void beforeCompilation(String source, String filename) {
        return;
    }
    
    @Override
    public void sourceCompiled(String source, String filename) {
        this.writeStdin(source);
        this.commandHistory.add(source);
    }
    
    @Override
    public void incompleteSource(String source, String filename) {
        return;
    }
    
    @Override
    public void sourceCompilationError(
        String source,
        String filename,
        PyException error)
    {
        this.writeStdin(source);
        this.commandHistory.add(source);
    }
        
    private transient final Style baseStyle;
    private transient final Style stdinStyle;
    private transient final Style stdoutStyle;
    private transient final Style stderrStyle;
    private transient final InterpreterOutputHandler stdoutHandler;
    private transient final InterpreterOutputHandler stderrHandler;
    private final ArrayList<String> commandHistory;
}
