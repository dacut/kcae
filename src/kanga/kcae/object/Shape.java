package kanga.kcae.object;

import java.io.Serializable;

import javax.annotation.CheckForNull;

public interface Shape extends Serializable {
    @CheckForNull
    public Rectangle getBoundingBox();
    public void setLineStyle(LineStyle lineStyle);
    public void setFillStyle(FillStyle fillStyle);
}