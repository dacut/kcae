package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

public class PushLocation extends ShapeInstruction {
    public PushLocation(boolean verticalOnly) {
        super(verticalOnly);
    }
    
    public static class Parser extends ShapeInstructionParser<PushLocation> {
        @Override
        public PushLocation parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            return new PushLocation(verticalOnly);
        }
        private static final long serialVersionUID = 1L;
    }
    
    private static final long serialVersionUID = 1L;
}
