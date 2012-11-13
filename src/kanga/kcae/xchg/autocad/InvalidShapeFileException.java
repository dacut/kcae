package kanga.kcae.xchg.autocad;

import java.io.IOException;

public class InvalidShapeFileException extends IOException {
    public InvalidShapeFileException() {
        super();
    }
    
    public InvalidShapeFileException(String message) {
        super(message);
    }
    
    public InvalidShapeFileException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidShapeFileException(Throwable cause) {
        super(cause);
    }

    private static final long serialVersionUID = 1L;
}