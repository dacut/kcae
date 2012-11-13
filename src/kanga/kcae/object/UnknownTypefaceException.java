package kanga.kcae.object;

public class UnknownTypefaceException extends IllegalArgumentException {
    public UnknownTypefaceException() {
        super();
    }
    
    public UnknownTypefaceException(String message) {
        super(message);
    }
    
    public UnknownTypefaceException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public UnknownTypefaceException(Throwable cause) {
        super(cause);
    }
    
    private static final long serialVersionUID = 1L;
}
