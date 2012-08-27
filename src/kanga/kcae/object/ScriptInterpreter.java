package kanga.kcae.object;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.python.core.CompileMode;
import org.python.core.Py;
import org.python.core.PyException;
import org.python.core.PyObject;
import org.python.util.InteractiveInterpreter;

public class ScriptInterpreter
    extends InteractiveInterpreter
    implements Serializable
{
    private static final long serialVersionUID = 1L;
    public static final int INITIAL_HISTORY_SIZE = 1000;
    
    public ScriptInterpreter() {
        super();
        this.history = new ArrayList<String>(INITIAL_HISTORY_SIZE);
        this.compileListeners = new HashSet<ScriptCompilationListener>();
    }
    
    public void addCompilationListener(final ScriptCompilationListener scl) {
        this.compileListeners.add(scl);
    }
    
    public void removeCompilationListener(final ScriptCompilationListener scl) {
        this.compileListeners.remove(scl);
    }
    
    @Override
    public boolean runsource(final String source, final String filename,
                             final CompileMode kind)
    {
        final PyObject code;
        for (final ScriptCompilationListener scl : this.compileListeners) {
            scl.beforeCompilation(source, filename);
        }
        
        try {
            this.history.add(source);
            code = Py.compile_command_flags(
                source, filename, kind, this.cflags, true);
        }
        catch (final PyException exc) {
            for (final ScriptCompilationListener scl : this.compileListeners) {
                scl.sourceCompilationError(source, filename, exc);
            }
            
            if (exc.match(Py.SyntaxError)) {
                // Case 1
                showexception(exc);
                return false;
            } else if (exc.match(Py.ValueError) || exc.match(Py.OverflowError)) {
                // Should not print the stack trace, just the error.
                showexception(exc);
                return false;
            } else {
                throw exc;
            }        
        }
        
        if (code == Py.None) {
            for (final ScriptCompilationListener scl : this.compileListeners) {
                scl.incompleteSource(source, filename);
            }
            
            return true;
        }
        
        for (final ScriptCompilationListener scl : this.compileListeners) {
            scl.sourceCompiled(source, filename);
        }

        runcode(code);
        return false;
    }
    
    private final ArrayList<String> history;
    private final Set<ScriptCompilationListener> compileListeners;
}
