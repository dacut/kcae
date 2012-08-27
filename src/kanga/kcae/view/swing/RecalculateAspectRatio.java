package kanga.kcae.view.swing;

import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import kanga.kcae.object.Rectangle;

/** Resize the view area to meet the aspect ratio when the MeasuredViewer
 *  is resized.
 */
class RecalculateAspectRatio extends ComponentAdapter {
    public RecalculateAspectRatio(final MeasuredView view) {
        this.view = view;
    }
    
    @Override
    public void componentResized(final ComponentEvent evt) {
        this.view.setViewArea(this.view.getViewArea(),
                              Rectangle.FitMethod.EXPAND);
    }
    
    private final MeasuredView view; 
}
