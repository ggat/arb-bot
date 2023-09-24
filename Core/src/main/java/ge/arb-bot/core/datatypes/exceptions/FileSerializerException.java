package ge.arb-bot.core.datatypes.exceptions;

/**
 * Created by giga on 9/30/17.
 */
public class FileSerializerException extends Exception {
    public FileSerializerException(String var1) {
        super(var1);
    }

    public FileSerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public FileSerializerException(Throwable cause) {
        super(cause);
    }
}
