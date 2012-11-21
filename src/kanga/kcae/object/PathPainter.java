package kanga.kcae.object;

public interface PathPainter {
    public void moveTo(
        Point p);
    
    public void lineTo(
        Point p);
    
    /** Draw an arc from the current point to the end point, with the start and
     *  end points at the specified angles on the arc.
     *  
     *  <p>
     *  The arc is always drawn from {@code startAngleRadians} to
     *  {@code endAngleRadians}.  Thus, if
     *  {@code startAngleRadians < endAngleRadians}, the curve is drawn
     *  counterclockwise; otherwise, the curve is drawn clockwise.
     *  </p>
     *  
     *  <p>
     *  Many drawing systems do not natively support arcs (including Java2D).
     *  These systems should use {@link ArcTo#paintAsBezier} to paint an
     *  approximate representation of the arc as Bézier curves.
     *  </p>
     *  
     *  @param center   The center of the arc.
     *  @param angleRadians The included angle of the arc, in radians.
     */
    public void arcTo(
        Point center,
        double angleRadians);
    
    public void quadraticCurveTo(
        Point control,
        Point target);
    
    public void bezierCurveTo(
        Point control1,
        Point control2,
        Point target);
    
    public void closePath();
}