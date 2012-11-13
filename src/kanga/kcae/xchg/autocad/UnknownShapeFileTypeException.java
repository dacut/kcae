package kanga.kcae.xchg.autocad;

public class UnknownShapeFileTypeException
    extends InvalidShapeFileException
{
    public UnknownShapeFileTypeException() {
        super();
    }
    
    public UnknownShapeFileTypeException(String message) {
        super(message);
    }
    
    public UnknownShapeFileTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownShapeFileTypeException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 1L;
}