package ge.arb-bot.core.datatypes.util;

import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Created by giga on 12/21/17.
 */
public class StreamUtils {

    public static <T> Collector<T, ?, T> singletonCollector() throws IllegalStateException {
        return Collectors.collectingAndThen(
                Collectors.toList(),
                list -> {
                    if (list.size() > 1) {
                        throw new IllegalStateException();
                        //return null;
                    } else if(list.size() < 1) {
                        return null;
                    }

                    return list.get(0);
                }
        );
    }
}
