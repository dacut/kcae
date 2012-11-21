package kanga.kcae.xchg.autocad;

import java.io.InputStream;
import java.io.IOException;
import java.io.Serializable;

public abstract class ShapeInstructionParser<T extends ShapeInstruction>
    implements Serializable
{
    /** Parse a directive from a SHX file.
     * 
     *  This assumes the specific instruction (leading byte) has already been
     *  read from the stream.
     * 
     *  @param is   The stream to read the bytes from.
     *  @param verticalOnly Whether a vertical-only prefix preceded this
     *              instruction.
     *  @param shapeId The id of the shape this directive is a part of.
     *  
     *  @return The parsed shape.
     *  
     *  @throws ArrayDone If this parser reads an array of instructions and
     *          the array is finished.
     *  @throws IOException If the underlying read operations fail.
     */
    public abstract T parse(
        InputStream is,
        boolean verticalOnly,
        int shapeId)
        throws IOException;
    
    /** Indicates whether this directive reads an array of instructions from
     *  the stream.
     *  
     *  @return {@code true} if the directive reads an array; {@code false}
     *          if it reads a single instruction.
     */
    public boolean isArray() { return false; }
    
    /** The exception raised by array instructions to indicate the array is
     *  finished.
     */
    static class ArrayDone extends Error {
        ArrayDone() { super(); }
        private static final long serialVersionUID = 1L;
    }
    
    protected static final ArrayDone arrayDone = new ArrayDone();
    private static final long serialVersionUID = 1L;
}