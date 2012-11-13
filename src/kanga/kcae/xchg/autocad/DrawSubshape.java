package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

public class DrawSubshape extends ShapeInstruction {
    public DrawSubshape(int shapeId, boolean verticalOnly) {
        super(verticalOnly);
        this.shapeId = shapeId;
        this.baseX = this.baseY = this.width = this.height = -1;
    }

    public DrawSubshape(
        int shapeId, 
        int baseX,
        int baseY,
        int width,
        int height,
        boolean verticalOnly)
    {
        super(verticalOnly);
        this.shapeId = shapeId;
        this.baseX = baseX;
        this.baseY = baseY;
        this.width = width;
        this.height = height;
    }

    
    public int getShapeId() {
        return this.shapeId;
    }
    
    public int getBaseX() {
        return this.baseX;
    }

    public int getBaseY() {
        return this.baseY;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }
    
    public static class Parser extends ShapeInstructionParser<DrawSubshape> {
        @Override
        public DrawSubshape parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            int op1, op2, op3, op4, op5, op6;
            
            op1 = is.read();
            if (op1 == -1) {
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }
            
            if (op1 != 0) {
                return new DrawSubshape(op1, verticalOnly);
            }
            
            // Bigfont subshape.
            op1 = is.read();
            op2 = is.read();
            op3 = is.read();
            op4 = is.read();
            op5 = is.read();
            op6 = is.read();

            if (op1 == -1 || op2 == -1 || op3 == -1 ||
                op4 == -1 || op5 == -1 || op6 == -1)
            {
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }
                
            int subshapeId = op1 * 256 + op2;
            int baseX = op3;
            int baseY = op4;
            int width = op5;
            int height = op6;
            
            return new DrawSubshape(
                subshapeId, baseX, baseY, width, height, verticalOnly);
        }

        private static final long serialVersionUID = 1L;
    }

    private final int shapeId;
    private final int baseX;
    private final int baseY;
    private final int width;
    private final int height;
    private static final long serialVersionUID = 1L;
}
