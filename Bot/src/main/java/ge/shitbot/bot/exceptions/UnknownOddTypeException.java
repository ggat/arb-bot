package ge.shitbot.bot.exceptions;

/**
 * Created by giga on 9/30/17.
 */
public class UnknownOddTypeException extends BookieDriverException {
    public UnknownOddTypeException(String var1) {
        super(var1);
    }

    public UnknownOddTypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownOddTypeException(Throwable cause) {
        super(cause);
    }
}
