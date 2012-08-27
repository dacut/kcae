package kanga.kcae.object;

import java.io.Serializable;

public interface Connectable extends Serializable {
    public SignalDirection getSignalDirection();
    public void setSignalDirection(SignalDirection direction);
    public Net getNet();
    public void setNet(Net net);
}