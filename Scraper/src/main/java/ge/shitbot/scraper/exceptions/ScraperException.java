package ge.shitbot.scraper.exceptions;

/**
 * Created by giga on 9/30/17.
 */
public class ScraperException extends Exception {
    public ScraperException(String var1) {
        super(var1);
    }

    public ScraperException(String message, Throwable cause) {
        super(message, cause);
    }

    public ScraperException(Throwable cause) {
        super(cause);
    }
}
