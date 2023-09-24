package ge.arb-bot.scraper.exceptions;

/**
 * Created by giga on 9/30/17.
 */
public class UncheckedScrapperException extends RuntimeException {
    public UncheckedScrapperException(String var1) {
        super(var1);
    }

    public UncheckedScrapperException(String message, Throwable cause) {
        super(message, cause);
    }

    public UncheckedScrapperException(Throwable cause) {
        super(cause);
    }
}
