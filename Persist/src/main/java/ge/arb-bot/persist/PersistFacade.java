package ge.arb-bot.persist;

import ge.arb-bot.persist.config.PersistConfig;
import ge.arb-bot.persist.util.SessionUtil;

import java.util.Map;

/**
 * Created by giga on 1/6/18.
 */
public class PersistFacade {

    public static void setSettings(PersistConfig map) {
        SessionUtil.setSettings(map);
    }
}
