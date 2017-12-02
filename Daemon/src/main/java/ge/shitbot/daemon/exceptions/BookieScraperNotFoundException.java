package ge.shitbot.daemon.exceptions;

/**
 * Created by giga on 9/30/17.
 */
public class BookieScraperNotFoundException extends Exception {
    public BookieScraperNotFoundException(String var1) {
        super(var1);
    }

    public BookieScraperNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public BookieScraperNotFoundException(Throwable cause) {
        super(cause);
    }
}
