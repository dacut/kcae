package kanga.kcae.object;

import java.io.Serializable;

public interface Shape extends Serializable {
    public Rectangle getBoundingBox();
    public void setLineStyle(LineStyle lineStyle);
    public void setFillStyle(FillStyle fillStyle);
}