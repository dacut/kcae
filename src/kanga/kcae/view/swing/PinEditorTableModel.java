package kanga.kcae.view.swing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.Pin;
import kanga.kcae.object.Symbol;
import kanga.kcae.object.event.PropertyChangeEvent;
import kanga.kcae.object.event.SymbolPinChangeListener;

public class PinEditorTableModel
    extends AbstractTableModel
    implements SymbolPinChangeListener
{
    private static final Log log = LogFactory.getLog(PinEditorTableModel.class);
    
    private static final String[] columnNames = new String[] {
        "Name", "Type", "Numbers"
    };
    
    public PinEditorTableModel(final Symbol symbol) {
        this.pins = new ArrayList<Pin>(symbol.getPins());
    }
    
    @Override
    public int getRowCount() {
        return this.pins.size();
    }
    
    @Override
    public int getColumnCount() {
        return 3;
    }
    
    @Override
    public String getColumnName(int column) {
        log.debug("columnName[" + column + "]=" + columnNames[column]);
        return columnNames[column];
    }
    
    @Override
    public Object getValueAt(int row, int column) {
        final Pin pin = this.pins.get(row);
            
        if (column == 0) {
            return pin.getName();
        }
        else if (column == 1) {
            return pin.getSignalDirection().toString();
        }
        else if (column == 2) {
            return pin.getPinNumber();
        }
        else {
            return null;
        }
    }

    @Override
    public void pinAdded(Symbol symbol, Pin pin) {
        this.pins = new ArrayList<Pin>(symbol.getPins());
        this.fireTableDataChanged();
    }

    @Override
    public void pinRemoved(Symbol symbol, Pin pin) {
        this.pins = new ArrayList<Pin>(symbol.getPins());
        this.fireTableDataChanged();
    }

    @Override
    public void pinChanged(
        Symbol symbol, Pin pin,
        Map<String, PropertyChangeEvent<?>> properties)
    {
        // No need to pick up changes; the pin objects are unchanged.
        // A redraw might be necessary, so figure out which pin has changed.
        
        for (int i = 0; i < this.pins.size(); ++i) {
            final Pin candidate = this.pins.get(i);
            if (pin == candidate) {
                final TableModelEvent evt = new TableModelEvent(this, i);
                this.fireTableChanged(evt);
                return;
            }
        }
        
        // Hmm... this pin doesn't seem to exist?!  Log an error, update our
        // view, and refresh everything.
        log.error("Received pinChanged event for pin " + pin +
                  ", but this pin is unknown to us.");
        this.pins = new ArrayList<Pin>(symbol.getPins());
        this.fireTableDataChanged();
        return;
    }

    
    private List<Pin> pins;
    private static final long serialVersionUID = 1L;
}
