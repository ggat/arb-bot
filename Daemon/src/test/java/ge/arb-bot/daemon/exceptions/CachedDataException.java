package ge.arb-bot.daemon.exceptions;

public class CachedDataException extends RuntimeException {
    public CachedDataException(String var1) {
        super(var1);
    }

    public CachedDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public CachedDataException(Throwable cause) {
        super(cause);
    }
}