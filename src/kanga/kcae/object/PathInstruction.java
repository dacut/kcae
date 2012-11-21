package kanga.kcae.object;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/** An instruction for painting a path.
 * 
 *  @see Path
 */
public interface PathInstruction
    extends Serializable, Transformable<PathInstruction>
{
    /** Visits the specified {@code PathPainter} using the method appropriate
     *  for the path instruction.
     *  
     *  @param pp       The {@code PathPainter} to visit.
     */
    public void paint(@Nonnull PathPainter pp);
    
    /** Updates the bounding box when performing a bounding box calculation.
     * 
     *  @param startPos The starting position when drawing the path.
     *  @param bbox     The bounding box of other elements in this path.
     *  @return The updated bounding box.
     */
    @Nonnull
    public Rectangle updateBoundingBox(
        @Nullable @CheckForNull Point startPos,
        @Nullable @CheckForNull Rectangle bbox);
    
    /** Updates the current position.
     * 
     *  @param startPos The position before the path instruction is executed.
     *  @return The position after the path instruction is executed.
     */
    @Nullable
    public Point updatePosition(@Nullable @CheckForNull Point startPos);
}