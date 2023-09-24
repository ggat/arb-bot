package ge.arb-bot.daemon.exceptions;

/**
 * Created by giga on 9/30/17.
 */
public class BookieNotFoundException extends Exception {
    public BookieNotFoundException(String var1) {
        super(var1);
    }

    public BookieNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookieNotFoundException(Throwable cause) {
        super(cause);
    }
}
