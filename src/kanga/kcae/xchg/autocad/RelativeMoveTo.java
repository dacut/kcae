package kanga.kcae.xchg.autocad;

import java.io.InputStream;
import java.io.IOException;

public class RelativeMoveTo extends ShapeInstruction {
    public RelativeMoveTo(int x, int y, boolean verticalOnly) {
        super(verticalOnly);
        this.x = x;
        this.y = y;
        return;
    }
    
    public int getX() {
        return this.x;
    }
    
    public int getY() {
        return this.y;
    }
    
    public static class Parser
        extends ShapeInstructionParser<RelativeMoveTo>
    {
        @Override
        public RelativeMoveTo parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
                throws IOException
        {
            int op1 = is.read();
            int op2 = is.read();
            if (op1 == -1 || op2 == -1) {
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }
        
            if (op1 >= 128) op1 -= 256;
            if (op2 >= 128) op2 -= 256;
        
            return new RelativeMoveTo(op1, op2, verticalOnly);
        }
        
        private static final long serialVersionUID = 1L;
    }
    
    public static class ArrayParser
        extends ShapeInstructionParser<RelativeMoveTo>
    {
        @Override
        public RelativeMoveTo parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            int op1 = is.read();
            int op2 = is.read();

            if (op1 == -1 || op2 == -1) {
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }

            if (op1 == 0 && op2 == 0) {
                throw arrayDone;
            }

            if (op1 >= 128) op1 -= 256;
            if (op2 >= 128) op2 -= 256;
                
            return new RelativeMoveTo(op1, op2, verticalOnly);
        }
        
        @Override
        public boolean isArray() { return true; }
        
        private static final long serialVersionUID = 1L;
    }
    
    private final int x;
    private final int y;
    private static final long serialVersionUID = 1L;
}
