package kanga.kcae.view.swing;

import javax.swing.JPanel;
import kanga.kcae.object.Schematic;

public class SchematicEditor extends JPanel {
    public SchematicEditor(final Schematic schematic) {
        this.schematic = schematic;
    }

    @SuppressWarnings("unused")
    private final Schematic schematic;
    
    private static final long serialVersionUID = 1L;
}
