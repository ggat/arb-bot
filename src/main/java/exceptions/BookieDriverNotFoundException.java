package exceptions;

/**
 * Created by giga on 9/30/17.
 */
public class BookieDriverNotFoundException extends Exception {
    public BookieDriverNotFoundException(String var1) {
        super(var1);
    }

    public BookieDriverNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookieDriverNotFoundException(Throwable cause) {
        super(cause);
    }
}
