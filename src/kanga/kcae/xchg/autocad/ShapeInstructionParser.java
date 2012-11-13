package kanga.kcae.xchg.autocad;

import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;

public abstract class ShapeInstructionParser<T extends ShapeInstruction>
    implements Serializable
{
    public abstract T parse(
        InputStream is,
        boolean verticalOnly,
        int shapeId)
        throws IOException;
    
    public boolean isArray() { return false; }
    
    static class ArrayDone extends Error {
        ArrayDone() { super(); }
        private static final long serialVersionUID = 1L;
    }
    
    protected static final ArrayDone arrayDone = new ArrayDone();
    
    private static final long serialVersionUID = 1L;
}