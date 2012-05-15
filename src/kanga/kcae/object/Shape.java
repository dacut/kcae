package kanga.kcae.object;

public abstract interface Shape {
    public Rectangle getBoundingBox();
    public LineStyle getLineStyle();
    public void setLineStyle(LineStyle lineStyle);
    public FillStyle getFillStyle();
    public void setFillStyle(FillStyle fillStyle);
}