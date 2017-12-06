package ge.shitbot.daemon.fetch;

/**
 * Created by giga on 12/6/17.
 */
@FunctionalInterface
public interface DataUpdateHandler {

    void handle(DataUpdateEvent event);
}
