package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

public class DeactivateDraw extends ShapeInstruction {
    public DeactivateDraw(boolean verticalOnly) {
        super(verticalOnly);
    }
    
    @Override
    public void visit(ShapeInstructionHandler handler) {
        handler.handle(this);
    }
    
    public static class Parser
        extends ShapeInstructionParser<DeactivateDraw>
    {
        @Override
        public DeactivateDraw parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            return new DeactivateDraw(verticalOnly);
        }
        
        private static final long serialVersionUID = 1L;
    }
    
    private static final long serialVersionUID = 1L;
}
