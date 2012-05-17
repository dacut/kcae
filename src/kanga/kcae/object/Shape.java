package kanga.kcae.object;

import java.awt.Graphics2D;

public interface Shape {
    public Rectangle getBoundingBox();
    public void setLineStyle(LineStyle lineStyle);
    public void setFillStyle(FillStyle fillStyle);
    public void draw(Graphics2D graphics);
}