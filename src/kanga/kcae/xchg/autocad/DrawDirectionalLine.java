package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

public class DrawDirectionalLine extends ShapeInstruction {
    public DrawDirectionalLine(int direction, int length, boolean verticalOnly)
    {
        super(verticalOnly);
        this.direction = direction;
        this.length = length;
    }
    
    public int getDirection() {
        return this.direction;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public static class Parser
        extends ShapeInstructionParser<DrawDirectionalLine>
    {
        @Override
        public DrawDirectionalLine parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            int code = is.read();
            if (code == -1) {
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }

            return new DrawDirectionalLine((code & 0xf0) >>> 4,
                                           (code & 0x0f),
                                           verticalOnly);
        }
        
        private static final long serialVersionUID = 1L;
    }   
    
    private final int direction;
    private final int length;
    private static final long serialVersionUID = 1L;
}
