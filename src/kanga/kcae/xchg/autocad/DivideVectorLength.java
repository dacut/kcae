package kanga.kcae.xchg.autocad;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class DivideVectorLength extends ShapeInstruction {
    public DivideVectorLength(int factor, boolean verticalOnly) {
        super(verticalOnly);
        this.factor = factor;
    }

    public int getFactor() {
        return this.factor;
    }
    
    @Override
    public void visit(ShapeInstructionHandler handler) {
        handler.handle(this);
    }
    
    @Override
    public boolean equals(Object otherObj) {
        if (! super.equals(otherObj)) { return false; }
        
        DivideVectorLength other = DivideVectorLength.class.cast(otherObj);
        return this.getFactor() == other.getFactor();
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(1038207767, 863317699)
            .appendSuper(super.hashCode())
            .append(this.getFactor())
            .toHashCode();
    }
    
    @Override
    protected ToStringBuilder toStringBuilder() {
        return super.toStringBuilder().append("factor", this.getFactor());
    }
    
    public static class Parser
        extends ShapeInstructionParser<DivideVectorLength>
    {
        @Override
        public DivideVectorLength parse(
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
            
            return new DivideVectorLength(op1, verticalOnly);
        }
        
        private static final long serialVersionUID = 1L;
    }

    private final int factor;
    private static final long serialVersionUID = 1L;
}
