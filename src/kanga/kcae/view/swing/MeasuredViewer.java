package kanga.kcae.view.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.BaseUnit;
import kanga.kcae.object.Extents;
import kanga.kcae.object.Rectangle;
import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.SwingConstants.VERTICAL;

public abstract class MeasuredViewer<T extends Component & MeasuredView>
    extends JPanel
{
    @SuppressWarnings("unused")
    private static final Log log = LogFactory.getLog(SymbolEditorFrame.class);
    
    class PanningListener
        extends MouseAdapter
        implements MouseMotionListener
    {
        private boolean enabled = true;
        private int lastX = Integer.MIN_VALUE;
        private int lastY = Integer.MIN_VALUE;
        
        @Override
        public void mouseDragged(final MouseEvent e) {
            final int x, y;
            
            if (! this.isEnabled()) {
                return;
            }
            
            x = e.getXOnScreen();
            y = e.getYOnScreen();
            
            if (this.lastX != Integer.MIN_VALUE &&
                this.lastY != Integer.MIN_VALUE)
            {
                // We have a previous reference point; use it to calculate the
                // distance in pixels.
                int dxPix = x - this.lastX;
                int dyPix = y - this.lastY;
                
                // Convert from pixels to quanta.
                final Ruler hRuler = MeasuredViewer.this.getHorizontalRuler();
                final Ruler vRuler = MeasuredViewer.this.getVerticalRuler();
                final long dxQua = -hRuler.getQuantaPerPixel() * dxPix;
                final long dyQua = -vRuler.getQuantaPerPixel() * dyPix;
             
                final Rectangle originalView = MeasuredViewer.this.getView();
                final Rectangle newView = originalView.translate(dxQua, dyQua);

                MeasuredViewer.this.setView(newView);
            }
            
            this.lastX = x;
            this.lastY = y;
            return;
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            return;
        }
        
        @Override
        public void mousePressed(final MouseEvent e) {
            if ((e.getButton() & MouseEvent.BUTTON1) != 0) {
                System.err.println("mousePressed:" + e);
                
                this.lastX = e.getXOnScreen();
                this.lastY = e.getYOnScreen();
            }
        }

        public boolean isEnabled() {
            return this.enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }

    protected MeasuredViewer(final T viewer, final BaseUnit baseUnit) {
        super(new GridBagLayout(), true);
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

        this.hRuler = new Ruler(-200000, 102000, HORIZONTAL, baseUnit);
        this.vRuler = new Ruler(-200000, 102000, VERTICAL, baseUnit);
        this.viewer = viewer;
        this.setLayout(layout);
        this.add(this.hRuler, hrcons);
        this.add(this.vRuler, vrcons);
        this.add(this.viewer, cpcons);
        
        this.panningListener = new PanningListener();
        this.viewer.addMouseListener(this.panningListener);
        this.viewer.addMouseMotionListener(this.panningListener);
    }

    public Ruler getHorizontalRuler() {
        return this.hRuler;
    }

    public Ruler getVerticalRuler() {
        return this.vRuler;
    }
    
    public Rectangle getView() {
        final Extents.Long hExtents = this.hRuler.getQuantaExtents();
        final Extents.Long vExtents = this.vRuler.getQuantaExtents();
        
        return Rectangle.fromExtents(hExtents, vExtents);
    }
    
    public void setView(final Rectangle r) {
        this.hRuler.setQuantaExtents(r.getHorizontalExtents());
        this.vRuler.setQuantaExtents(r.getVerticalExtents());
    }

    public T getViewer() {
        return this.viewer;
    }
    
    private final Ruler hRuler;
    private final Ruler vRuler;
    private final T viewer;
    private final PanningListener panningListener;
    private static final long serialVersionUID = -7664541928680432846L;	
}
