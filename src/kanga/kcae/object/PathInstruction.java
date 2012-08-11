package kanga.kcae.object;

/** An instruction for painting a path.
 * 
 *  @see Path
 */
public interface PathInstruction {
    /** Visits the specified {@code PathPainter} using the method appropriate
     *  for the path instruction.
     *  
     *  @param pp       The {@code PathPainter} to visit.
     */
    public void paint(PathPainter pp);
    
    /** Updates the bounding box when performing a bounding box calculation.
     * 
     *  @param startPos The starting position when drawing the path.
     *  @param bbox     The bounding box of other elements in this path.
     *  @return The updated bounding box.
     */
    public Rectangle updateBoundingBox(Point startPos, Rectangle bbox);
    
    /** Updates the current position.
     * 
     *  @param startPos The position before the path instruction is executed.
     *  @return The position after the path instruction is executed.
     */
    public Point updatePosition(Point startPos);
}