package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

/** AutoCAD instruction to start drawing.
 *  
 *  <p>Unlike KCAE, AutoCAD does not have separate moveto and lineto
 *  directives; they are all encoded as {@link RelativeMoveTo} directives.
 *  A line is drawn if drawing is active.  Drawing is active at the start of
 *  the shape and after a {@code ActivateDraw} instruction.</p>
 */
public class ActivateDraw extends ShapeInstruction {
    /** Create a new ActivateDraw instruction.
     * 
     *  @param verticalOnly If true, this instruction applies only when the
     *         shape is being drawn in a vertical orientation.
     */
    public ActivateDraw(boolean verticalOnly) {
        super(verticalOnly);
    }
    
    @Override
    public void visit(ShapeInstructionHandler handler) {
        handler.handle(this);
    }
    
    public static class Parser extends ShapeInstructionParser<ActivateDraw> {
        @Override
        public ActivateDraw parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            return new ActivateDraw(verticalOnly);
        }
        
        private static final long serialVersionUID = 1L;
    }
    
    private static final long serialVersionUID = 1L;
}
