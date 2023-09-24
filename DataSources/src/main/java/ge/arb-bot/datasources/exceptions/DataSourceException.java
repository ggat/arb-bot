package ge.arb-bot.datasources.exceptions;

/**
 * Created by giga on 9/30/17.
 */
public class DataSourceException extends Exception {
    public DataSourceException(String var1) {
        super(var1);
    }

    public DataSourceException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataSourceException(Throwable cause) {
        super(cause);
    }
}
