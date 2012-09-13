package kanga.kcae.view.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.python.core.PyBoolean;
import org.python.core.PyDictionary;
import org.python.core.PyFloat;
import org.python.core.PyInteger;
import org.python.core.PyLong;
import org.python.core.PyNone;
import org.python.core.PyObject;
import org.python.core.PyString;

public class ObjectDebugFrame extends JFrame {
    private static final Log log = LogFactory.getLog(ObjectDebugFrame.class);
    
    class TableClickHandler extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent evt) {
            if (evt.getButton() == MouseEvent.BUTTON1 &&
                evt.getClickCount() == 2)
            {
                ObjectDebugFrame.this.handleDoubleClick(evt);
            }
        }
    }
    
    class ExpressionChangedHandler implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            ObjectDebugFrame.this.updateDebugger();
        }
        
    }
    
    public static class HistoryEntry {
        public HistoryEntry(final String expression, final PyObject target) {
            this.expression = expression;
            this.target = target;
        }
        
        @Override
        public String toString() {
            return this.expression;
        }
        
        final String expression;
        final PyObject target;
    }
    
    public ObjectDebugFrame(final String expression, final PyObject target) {
        super("Debug");
        
        final Container contentPane = this.getContentPane();
        contentPane.setLayout(new BorderLayout());
        
        log.debug("debug: expression='" + expression + "', target=" + target);

        // Create the widgets.
        this.expressionField = new JComboBox();
        this.table = new JTable();
        this.table.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        JScrollPane tableScroll = new JScrollPane(
            this.table, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        contentPane.add(this.expressionField, BorderLayout.NORTH);
        contentPane.add(tableScroll, BorderLayout.CENTER);
        
        // Listen for double click events on the table.
        this.table.addMouseListener(new TableClickHandler());
        
        // Move this to the right side of the screen.
        final GraphicsConfiguration screen = this.getGraphicsConfiguration();
        final Rectangle screenSize = screen.getBounds();
        final Rectangle bounds = new Rectangle(
            screenSize.width - 200, 0, 200, screenSize.height);
        this.setBounds(bounds);
        this.setAlwaysOnTop(true);
        
        this.expressionField.addItem(new HistoryEntry(expression, target));
        this.expressionField.addActionListener(new ExpressionChangedHandler());

        // Update the expression and table widgets.
        this.updateDebugger();
        
        return;
    }
    
    public void updateDebugger() {
        this.tableModel = getTableModel(this.getTarget());
        this.table.setModel(this.tableModel);
        this.repaint();
    }
    
    public static GenericDebugTableModel<? extends PyObject> getTableModel(
        final PyObject target)
    {
        if (target instanceof PyNone)
            return new PyNoneDebugTableModel((PyNone) target);
        else if (target instanceof PyDictionary)
            return new PyDictionaryDebugTableModel((PyDictionary) target);
        else if (target instanceof PyBoolean)
            return new PyPrimitiveDebugTableModel((PyBoolean) target);
        else if (target instanceof PyInteger)
            return new PyPrimitiveDebugTableModel((PyInteger) target);
        else if (target instanceof PyLong)
            return new PyPrimitiveDebugTableModel((PyLong) target);
        else if (target instanceof PyFloat)
            return new PyPrimitiveDebugTableModel((PyFloat) target);
        else if (target instanceof PyString)
            return new PyPrimitiveDebugTableModel((PyString) target);
        else
            return new PyObjectDebugTableModel(target);
    }
    
    public void handleDoubleClick(final MouseEvent evt) {
        int row = this.table.rowAtPoint(evt.getPoint());
        
        // row == -1 indicates no row at that point.
        // rows 0 and 1 are reserved for type information.
        
        if (row < 2)
            return;
        
        Object attribute = this.tableModel.getValueAt(row, 0);
        Object value = this.tableModel.getValueAt(row, 1);
        if (value instanceof PyObject) {
            // Remove anything after this in the history.
            final int curIndex = this.expressionField.getSelectedIndex();
            final int numItems = this.expressionField.getItemCount();
            final HistoryEntry hent =
                (HistoryEntry) this.expressionField.getItemAt(curIndex);
            
            for (int i = numItems - 1; i > curIndex; --i) {
                this.expressionField.removeItemAt(i);
            }
            
            this.expressionField.addItem(new HistoryEntry(
                hent.expression + "." + attribute.toString(),
                (PyObject) value));
            this.expressionField.setSelectedIndex(curIndex + 1);
            this.updateDebugger();
        }
    }

    public PyObject getTarget() {
        final HistoryEntry hent =
            (HistoryEntry) this.expressionField.getSelectedItem();
        return hent.target;
    }

    private final JComboBox expressionField;
    private final JTable table;
    private GenericDebugTableModel<? extends PyObject> tableModel;
    
    private static final long serialVersionUID = 1L;
}
