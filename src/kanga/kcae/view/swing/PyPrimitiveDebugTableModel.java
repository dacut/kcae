package kanga.kcae.view.swing;

import org.python.core.PyBoolean;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyObject;
import org.python.core.PyString;

public class PyPrimitiveDebugTableModel
    extends GenericDebugTableModel<PyObject>
{
    public PyPrimitiveDebugTableModel(PyBoolean target) {
        this((PyObject) target);
    }
    
    public PyPrimitiveDebugTableModel(PyInteger target) {
        this((PyObject) target);
    }

    public PyPrimitiveDebugTableModel(PyLong target) {
        this((PyObject) target);
    }
    
    public PyPrimitiveDebugTableModel(PyFloat target) {
        this((PyObject) target);
    }
    
    public PyPrimitiveDebugTableModel(PyString target) {
        this((PyObject) target);
    }

    private PyPrimitiveDebugTableModel(PyObject target) {
        super(target);
        this.addField("Value", target);
    }
    
    private static final long serialVersionUID = 1L;
}
