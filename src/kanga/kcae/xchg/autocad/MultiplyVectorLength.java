package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class MultiplyVectorLength extends ShapeInstruction {
    public MultiplyVectorLength(int factor, boolean verticalOnly) {
        super(verticalOnly);
        this.factor = factor;
    }

    public int getFactor() {
        return this.factor;
    }
    
    @Override
    public boolean equals(Object otherObj) {
        if (! super.equals(otherObj)) { return false; }
        
        MultiplyVectorLength other = MultiplyVectorLength.class.cast(otherObj);
        return this.getFactor() == other.getFactor();
    }

    @Override
    public void visit(ShapeInstructionHandler handler) {
        handler.handle(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17156089, 687512491)
            .appendSuper(super.hashCode())
            .append(this.getFactor())
            .toHashCode();
    }
    
    @Override
    protected ToStringBuilder toStringBuilder() {
        return super.toStringBuilder().append("factor", this.getFactor());
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
