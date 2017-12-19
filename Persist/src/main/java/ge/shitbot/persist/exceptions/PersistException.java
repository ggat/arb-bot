package ge.shitbot.persist.exceptions;

/**
 * Created by giga on 9/30/17.
 */
public class PersistException extends Exception {
    public PersistException(String var1) {
        super(var1);
    }

    public PersistException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersistException(Throwable cause) {
        super(cause);
    }
}
