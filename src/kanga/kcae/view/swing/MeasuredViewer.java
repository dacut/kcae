package kanga.kcae.view.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.SwingConstants.VERTICAL;

public abstract class MeasuredViewer<T extends Component> extends JPanel {
    protected MeasuredViewer(final T viewer, final long baseUnit) {
        super(new GridBagLayout(), true);
        final Container contentPane = this.getRootPane().getContentPane();
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints cpcons = new GridBagConstraints();
        final GridBagConstraints hrcons = new GridBagConstraints();
        final GridBagConstraints vrcons = new GridBagConstraints();

        cpcons.gridx = 1;
        cpcons.gridy = 1;
        cpcons.fill = GridBagConstraints.BOTH;
        cpcons.weightx = 1.0;
        cpcons.weighty = 1.0;
        
        hrcons.gridx = 1;
        hrcons.gridy = 0;
        hrcons.fill = GridBagConstraints.BOTH;
        hrcons.weightx = 1.0;
        
        vrcons.gridx = 0;
        vrcons.gridy = 1;
        vrcons.fill = GridBagConstraints.BOTH;
        vrcons.weighty = 1.0;

        this.hRuler = new Ruler(-200000, 102000, HORIZONTAL);
        this.vRuler = new Ruler(-200000, 102000, VERTICAL);
        this.viewer = viewer;
        contentPane.setLayout(layout);
        contentPane.add(this.hRuler, hrcons);
        contentPane.add(this.vRuler, vrcons);
        contentPane.add(this.viewer, cpcons);
}

    public Ruler getHorizontalRuler() {
        return this.hRuler;
    }

    public Ruler getVerticalRuler() {
        return this.vRuler;
    }

    public void setView(
            final long xMin,
            final long yMin,
            final long xMax,
            final long yMax)
    {
        this.hRuler.setMin(xMin);
        this.hRuler.setMax(xMax);
        this.vRuler.setMin(yMin);
        this.vRuler.setMax(yMax);
    }

    public T getViewer() {
        return this.viewer;
    }

    private final Ruler hRuler;
    private final Ruler vRuler;
    private final T viewer;
    private static final long serialVersionUID = -7664541928680432846L;	
}
