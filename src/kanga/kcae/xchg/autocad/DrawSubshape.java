package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DrawSubshape extends ShapeInstruction {
    public DrawSubshape(char shapeId, boolean verticalOnly) {
        super(verticalOnly);
        this.subshapeId = shapeId;
        this.baseX = this.baseY = this.width = this.height = -1;
    }

    public DrawSubshape(
        char subshapeId, 
        int baseX,
        int baseY,
        int width,
        int height,
        boolean verticalOnly)
    {
        super(verticalOnly);
        this.subshapeId = subshapeId;
        this.baseX = baseX;
        this.baseY = baseY;
        this.width = width;
        this.height = height;
    }

    
    public char getSubshapeId() {
        return this.subshapeId;
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
    
    @Override
    public void visit(ShapeInstructionHandler handler) {
        handler.handle(this);
    }
    
    @Override
    public boolean equals(Object otherObj) {
        if (! super.equals(otherObj)) { return false; }
        
        DrawSubshape other = DrawSubshape.class.cast(otherObj);
        return new EqualsBuilder()
            .append(this.getSubshapeId(), other.getSubshapeId())
            .append(this.getBaseX(), other.getBaseX())
            .append(this.getBaseY(), other.getBaseY())
            .append(this.getWidth(), other.getWidth())
            .append(this.getHeight(), other.getHeight())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1641303973, 1506208421)
            .append(this.getSubshapeId())
            .append(this.getBaseX())
            .append(this.getBaseY())
            .append(this.getWidth())
            .append(this.getHeight())
            .toHashCode();
    }

    @Override
    protected ToStringBuilder toStringBuilder() {
        return super.toStringBuilder()
            .append("subshapeId", this.getSubshapeId())
            .append("baseX", this.getBaseX())
            .append("baseY", this.getBaseY())
            .append("width", this.getWidth())
            .append("height", this.getHeight());
    }
    
    public static class Parser extends ShapeInstructionParser<DrawSubshape> {
        @Override
        public DrawSubshape parse(
            InputStream is,
            boolean verticalOnly,
            int shapeId)
            throws IOException
        {
            int subshapeIdHigh;
            int subshapeIdLow;
            int baseX;
            int baseY;
            int width;
            int height;
            
            subshapeIdHigh = is.read();
            if (subshapeIdHigh == -1) {
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }
            
            if (subshapeIdHigh != 0) {
                return new DrawSubshape((char) subshapeIdHigh, verticalOnly);
            }
            
            // Bigfont subshape.
            subshapeIdHigh = is.read();
            subshapeIdLow = is.read();
            baseX = is.read();
            baseY = is.read();
            width = is.read();
            height = is.read();

            if (subshapeIdHigh == -1 || subshapeIdLow == -1 || baseX == -1 ||
                baseY == -1 || width == -1 || height == -1)
            {
                throw new InvalidShapeFileException(
                    "Shape " + shapeId + " truncated");
            }
                
            char subshapeId = (char) (subshapeIdHigh * 256 + subshapeIdLow);
            return new DrawSubshape(
                subshapeId, baseX, baseY, width, height, verticalOnly);
        }

        private static final long serialVersionUID = 1L;
    }

    private final char subshapeId;
    private final int baseX;
    private final int baseY;
    private final int width;
    private final int height;
    private static final long serialVersionUID = 1L;
}
