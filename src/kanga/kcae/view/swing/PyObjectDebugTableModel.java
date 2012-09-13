package kanga.kcae.view.swing;

import java.util.TreeSet;

import org.python.core.PyCompoundCallable;
import org.python.core.PyMethod;
import org.python.core.PyObject;

public class PyObjectDebugTableModel
    extends GenericDebugTableModel<PyObject>
{
    public PyObjectDebugTableModel(final PyObject target) {
        super(target);
        
        final PyObject dir = target.__dir__();
        if (dir == null)
            return;
        
        final TreeSet<String> keys = new TreeSet<String>();
        
        PyObject dirIter = dir.__iter__();
        
        for (PyObject key = dirIter.__iternext__();
             key != null;
             key = dirIter.__iternext__())
        {
            String keyStr = key.toString();
            
            if (keyStr.startsWith("__") && keyStr.endsWith("__"))
                continue;
            
            keys.add(keyStr);
        }

        for (String keyStr : keys) {
            try {
                PyObject value = target.__getattr__(keyStr);
                if (!(value instanceof PyMethod ||
                      value instanceof PyCompoundCallable))
                {
                    this.addField(keyStr, value);
                }
            }
            catch (Exception e) {
                // Ignore write-only attributes
            }
        }
        
        // Add methods
        for (String keyStr : keys) {
            try {
                PyObject value = target.__getattr__(keyStr);
                if ((value instanceof PyMethod ||
                     value instanceof PyCompoundCallable))
                {
                    this.addField(keyStr, value);
                }
            }
            catch (Exception e) {
                // Ignore write-only attributes
            }
        }
        
    }

    private static final long serialVersionUID = 1L;
}
