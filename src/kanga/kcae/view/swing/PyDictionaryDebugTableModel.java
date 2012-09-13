package kanga.kcae.view.swing;

import org.python.core.PyDictionary;
import org.python.core.PyList;
import org.python.core.PyObject;

public class PyDictionaryDebugTableModel
    extends GenericDebugTableModel<PyDictionary>
{
    public PyDictionaryDebugTableModel(PyDictionary target) {
        super(target);
        
        // Sort the keys.  Since Jython (as of 2.7a2, at least) doesn't expose
        // the sorted() function, we emulate it here by copying the key list
        // and sorting the copy.
        PyList keys = new PyList(target.keys().getArray());
        keys.sort();
        
        PyObject keyIter = keys.__iter__();
        for (PyObject key = keyIter.__iternext__(); key != null;
             key = keyIter.__iternext__())
        {
            this.addField(key.toString(), target.get(key));
        }
    }

    private static final long serialVersionUID = 1L;
}
