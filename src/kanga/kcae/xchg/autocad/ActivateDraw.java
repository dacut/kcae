package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

public class ActivateDraw extends ShapeInstruction {
    public ActivateDraw(boolean verticalOnly) {
        super(verticalOnly);
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
