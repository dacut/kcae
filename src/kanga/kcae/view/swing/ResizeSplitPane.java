package kanga.kcae.view.swing;

import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;

import javax.swing.JSplitPane;

/** The {@code ResizeSplitPane} class resizes a JSplitPane container to a
 *  proportional size upon rendering.
 *  
 *  <p>The {@code JSplitPane#setDividerLocation(double)} method cannot be
 *  usefully called on an unrealized JSplitPane since this depends on the
 *  current size of the pane (which is 0x0 for unrealized panes).  To get
 *  around this limitation, the ResizeSplitPane object will listen for the
 *  first resize event on the component, invoke
 *  {@code JSplitPane#setDividerLocation(double)}, and deregister itself as a
 *  component listener.</p>
 *  
 *  <p>Using this class:
 *  <pre>    JSplitPane pane = new JSplitPane();
 *      pane.addComponentListener(new ResizeSplitPane(0.8));
 *  </pre></p>
 */
public class ResizeSplitPane extends ComponentAdapter {
    public static ArrayList<String> dlog = new ArrayList<String>(); 
    ResizeSplitPane(final double location) {
        this.location = location;
    }
    
    @Override
    public void componentResized(ComponentEvent e) {
        final Component component = e.getComponent();
        final JSplitPane pane = (JSplitPane) component;
        
        dlog.add("height: " + pane.getHeight());
        pane.setDividerLocation(this.location);
        dlog.add("divider location: " + pane.getDividerLocation());
        //component.removeComponentListener(this);            
    }
    
    private final double location;
}