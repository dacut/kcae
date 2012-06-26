package kanga.kcae.object;

import org.python.core.PyException;

public interface ScriptCompilationListener {
    public void beforeCompilation(String source, String filename);
    public void sourceCompiled(String source, String filename);
    public void incompleteSource(String source, String filename);
    public void sourceCompilationError(String source, String filename,
                                       PyException error);
}
