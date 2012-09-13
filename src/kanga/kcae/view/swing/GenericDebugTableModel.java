package kanga.kcae.view.swing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import org.python.core.PyObject;

public abstract class GenericDebugTableModel<T extends PyObject>
    extends AbstractTableModel
{
    public GenericDebugTableModel(T target) {
        this.target = target;
        this.fields = new ArrayList<Object[]>();
        
        this.fields.add(
            new Object[] { "Python Type", target.getType().getName() });
        this.fields.add(
            new Object[] { "Java Type", target.getClass().getName() });
        
        return;
    }
    
    protected void addField(String name, String value) {
        this.fields.add(new Object[] { name, value });
    }
    
    protected void addField(String name, PyObject value) {
        this.fields.add(new Object[] { name, value });
    }

    @Override
    public final int getRowCount() {
        return this.fields.size();
    }

    @Override
    public final int getColumnCount() {
        return 2;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return this.fields.get(rowIndex)[columnIndex];
    }
    
    public T getTarget() {
        return this.target;
    }
    
    private final T target;
    private final List<Object[]> fields;
    
    private static final long serialVersionUID = 1L;
}
