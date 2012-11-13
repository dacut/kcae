package kanga.kcae.object;

import javax.annotation.Nonnull;

public interface Transformable<T extends Transformable<T>> {
    /** Scales the object by the specified amount from the origin.
     *
     *  @param  factor   The amount to scale by.
     *  @return A copy of this object scaled by the specified amount from the
     *          origin.
     */
    @Nonnull
    public T scale(double factor);

    /** Translates the object by the specified amount in the x and y directions.
     *
     *  @param  dx       The amount to translate in the x direction.
     *  @param  dy       The amount to translate in the y direction.
     *  @return A copy of this object translated by (dx, dy).
     */
    @Nonnull
    public T translate(long dx, long dy);
    
    /** Rotates this object through the specified number of quadrants.
     * 
     *  Positive values rotate the positive x-axis toward the positive y-axis.
     * 
     *  @param nQuadrants       The number of quadrants to rotate through.
     *  @return A copy of this object rotated by the specified number of
     *          quadrants.
     */
    @Nonnull
    public T rotateQuadrant(int nQuadrants);
}
