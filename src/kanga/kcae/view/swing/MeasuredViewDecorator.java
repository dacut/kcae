package kanga.kcae.view.swing;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import static javax.swing.SwingConstants.HORIZONTAL;
import static javax.swing.SwingConstants.VERTICAL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kanga.kcae.object.BaseUnit;
import kanga.kcae.object.Rectangle;

/** A component which places rulers around a MeasuredView, attaches a 
 *  MeasuredViewTool to it, and dispatches input events to the tool.
 * 
 *  This component ensures that the view areas of the {@link MeasuredView}
 *  and the surrounding {@link Ruler} objects stay in sync.
 *  
 *  @param <T>  The actual type of the {@code MeasuredView} object.
 */
public abstract class MeasuredViewDecorator<T extends Component & MeasuredView>
    extends JPanel
    implements ViewAreaChangeListener
{
    static final Log log = LogFactory.getLog(MeasuredViewDecorator.class);
    
    /** Create a new MeasuredViewer container with a null view area.
     * 
     *  @param view     The interior view.
     *  @param baseUnit The base units to use for drawing ruler ticks and
     *      labels.
     */
    protected MeasuredViewDecorator(
        final T viewer,
        final BaseUnit baseUnit)
    {
        this(viewer, null, baseUnit);
    }

    /** Create a new MeasuredViewer container.
     * 
     *  @param view     The interior view.
     *  @param viewArea The initial view area, or {@code null} if the initial
     *      view area is undefined.
     *  @param baseUnit The base units to use for drawing ruler ticks and
     *      labels.
     */
    protected MeasuredViewDecorator(
        final T view,
        final Rectangle viewArea,
        final BaseUnit baseUnit)
    {
        super(new GridBagLayout(), true);
        final GridBagLayout layout = new GridBagLayout();
        final GridBagConstraints cpcons = new GridBagConstraints();
        final GridBagConstraints hrcons = new GridBagConstraints();
        final GridBagConstraints vrcons = new GridBagConstraints();
        final GridBagConstraints imgcons = new GridBagConstraints();

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

        imgcons.gridx = 0;
        imgcons.gridy = 2;
        imgcons.fill = GridBagConstraints.BOTH;

        this.hRuler = new Ruler(HORIZONTAL, baseUnit);
        this.vRuler = new Ruler(VERTICAL, baseUnit);
        this.viewer = view;

        this.setLayout(layout);
        this.add(this.hRuler, hrcons);
        this.add(this.vRuler, vrcons);
        this.add(this.viewer, cpcons);

        view.addViewAreaChangeListener(this);
        view.setViewArea(viewArea, Rectangle.FitMethod.NEAREST);
    }

    /** Returns the horizontal ruler component.
     * 
     *  @return The horizontal ruler.
     */
    public Ruler getHorizontalRuler() {
        return this.hRuler;
    }

    /** Returns the vertical ruler component.
     * 
     *  @return The vertical ruler.
     */
    public Ruler getVerticalRuler() {
        return this.vRuler;
    }

    @Override
    public void viewAreaChanged(ViewAreaChangeEvent evt) {
        final Rectangle viewArea = evt.getNewViewArea();

        if (viewArea != null) {
            this.hRuler.setQuantaExtents(viewArea.getHorizontalExtents());
            this.vRuler.setQuantaExtents(viewArea.getVerticalExtents());
        } else {
            this.hRuler.setQuantaExtents(null);
            this.vRuler.setQuantaExtents(null);
        }
    }
    
    public T getViewer() {
        return this.viewer;
    }

    private final Ruler hRuler;
    private final Ruler vRuler;
    private final T viewer;
    private static final long serialVersionUID = 1L;
}
