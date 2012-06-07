package kanga.kcae.object;

public interface Shape {
    public Rectangle getBoundingBox();
    public void setLineStyle(LineStyle lineStyle);
    public void setFillStyle(FillStyle fillStyle);
}