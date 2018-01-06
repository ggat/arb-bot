package ge.shitbot.persist;

import ge.shitbot.persist.config.PersistConfig;
import ge.shitbot.persist.util.SessionUtil;

import java.util.Map;

/**
 * Created by giga on 1/6/18.
 */
public class PersistFacade {

    public static void setSettings(PersistConfig map) {
        SessionUtil.setSettings(map);
    }
}
