package ge.shitbot.core.datatypes.exceptions;

/**
 * Created by giga on 9/30/17.
 */
public class CoreException extends Exception {
    public CoreException(String var1) {
        super(var1);
    }

    public CoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public CoreException(Throwable cause) {
        super(cause);
    }
}
