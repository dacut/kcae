package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

public class MultiplyVectorLength extends ShapeInstruction {
    public MultiplyVectorLength(int factor, boolean verticalOnly) {
        super(verticalOnly);
        this.factor = factor;
    }

    public int getFactor() {
        return this.factor;
    }
    
    public static class Parser
        extends ShapeInstructionParser<MultiplyVectorLength>
    {
        @Override
        public MultiplyVectorLength parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            int op1 = is.read();
            if (op1 == -1) {
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }
            
            return new MultiplyVectorLength(op1, verticalOnly);
        }
        
        private static final long serialVersionUID = 1L;
    }

    private final int factor;
    private static final long serialVersionUID = 1L;
}
