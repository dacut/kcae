package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

public class PopLocation extends ShapeInstruction {
    public PopLocation(boolean verticalOnly) {
        super(verticalOnly);
    }
    
    public static class Parser extends ShapeInstructionParser<PopLocation> {
        @Override
        public PopLocation parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            return new PopLocation(verticalOnly);
        }
        private static final long serialVersionUID = 1L;
    }
    
    private static final long serialVersionUID = 1L;
}
