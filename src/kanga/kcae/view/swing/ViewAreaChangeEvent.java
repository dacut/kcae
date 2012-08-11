package kanga.kcae.view.swing;

import java.util.EventObject;
import kanga.kcae.object.Rectangle;

public class ViewAreaChangeEvent extends EventObject {
    private static final long serialVersionUID = 1L;
    
    public ViewAreaChangeEvent(
        final MeasuredView view,
        final Rectangle oldViewArea,
        final Rectangle newViewArea)
    {
        super(view);
        this.oldViewArea = oldViewArea;
        this.newViewArea = newViewArea;
        return;
    }
    
    public MeasuredView getView() {
        return (MeasuredView) this.getSource();
    }
    
    public Rectangle getOldViewArea() {
        return this.oldViewArea;
    }
    
    public Rectangle getNewViewArea() {
        return this.newViewArea;
    }
    
    private final Rectangle oldViewArea;
    private final Rectangle newViewArea;
}
