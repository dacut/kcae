package kanga.kcae.object;

public enum Orientation {
    R0() {
        @Override
        public Orientation rotateLeft() {
            return Orientation.R0;
        }
    };
    
    public abstract Orientation rotateLeft();
}
