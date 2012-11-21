package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DrawDirectionalLine extends ShapeInstruction {
    public DrawDirectionalLine(
        @Nonnull final DirectionCode directionCode,
        final int length,
        final boolean verticalOnly)
    {
        super(verticalOnly);
        this.directionCode = directionCode;
        this.length = length;
    }
    
    public DirectionCode getDirectionCode() {
        return this.directionCode;
    }
    
    public int getLength() {
        return this.length;
    }
    
    @Override
    public void visit(ShapeInstructionHandler handler) {
        handler.handle(this);
    }

    @Override
    public boolean equals(Object otherObj) {
        if (! super.equals(otherObj)) { return false; }
        
        DrawDirectionalLine other = DrawDirectionalLine.class.cast(otherObj);
        return new EqualsBuilder()
            .append(this.getDirectionCode(), other.getDirectionCode())
            .append(this.getLength(), other.getLength())
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(1925865391, 860981669)
            .appendSuper(super.hashCode())
            .append(this.getDirectionCode())
            .append(this.getLength())
            .toHashCode();
    }

    @Override
    protected ToStringBuilder toStringBuilder() {
        return super.toStringBuilder()
            .append("direction", this.getDirectionCode())
            .append("length", this.getLength());
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
            
            int directionCode = (code & 0xf0) >>> 4;
            int length = (code & 0x0f);

            return new DrawDirectionalLine(
                DirectionCode.forCode(directionCode),
                length,
                verticalOnly);
        }
        
        private static final long serialVersionUID = 1L;
    }   
    
    private final DirectionCode directionCode;
    private final int length;
    private static final long serialVersionUID = 1L;
}
