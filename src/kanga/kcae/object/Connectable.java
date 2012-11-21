package kanga.kcae.object;

import java.io.Serializable;

import javax.annotation.CheckForNull;
import javax.annotation.Nonnull;

public interface Connectable extends Serializable {
    @Nonnull
    public SignalDirection getSignalDirection();
    
    public void setSignalDirection(@Nonnull SignalDirection direction);
    
    @CheckForNull
    public Net getNet();
    
    public void setNet(@CheckForNull Net net);
}