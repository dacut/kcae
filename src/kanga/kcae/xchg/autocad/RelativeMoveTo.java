package kanga.kcae.xchg.autocad;

import java.io.InputStream;
import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

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
    
    @Override
    public void visit(ShapeInstructionHandler handler) {
        handler.handle(this);
    }

    @Override
    public boolean equals(Object otherObj) {
        if (! super.equals(otherObj)) { return false; }
        
        RelativeMoveTo other = RelativeMoveTo.class.cast(otherObj);
        return new EqualsBuilder()
            .append(this.getX(), other.getX())
            .append(this.getY(), other.getX())
            .isEquals();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(695564491, 97721467)
            .appendSuper(super.hashCode())
            .append(this.getX())
            .append(this.getY())
            .toHashCode();
    }
    
    @Override
    protected ToStringBuilder toStringBuilder() {
        return super.toStringBuilder()
            .append("x", this.getX())
            .append("y", this.getY());
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
