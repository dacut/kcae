package kanga.kcae.object;

public interface Connectable {
    public SignalDirection getDirection();
    public void setDirection(SignalDirection direction);
    public Net getNet();
    public void setNet(Net net);
}