package ge.shitbot.scraper.exceptions;

/**
 * Created by giga on 9/30/17.
 */
public class ScrapperException extends Exception {
    public ScrapperException(String var1) {
        super(var1);
    }

    public ScrapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScrapperException(Throwable cause) {
        super(cause);
    }
}
