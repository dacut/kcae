package kanga.kcae.view.swing;

import javax.swing.JTable;

import kanga.kcae.object.Symbol;

public class PinEditorTable extends JTable {
    public PinEditorTable(final Symbol symbol) {
        super(new PinEditorTableModel(symbol));
    }
    
    private static final long serialVersionUID = 1L;
}
