package ge.shitbot.persist.config;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by giga on 1/6/18.
 */
public class PersistConfig extends HashMap<String, String> {

    public PersistConfig() {
    }

    public PersistConfig(Map<? extends String, ? extends String> m) {
        super(m);
    }

}