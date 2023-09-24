package ge.arb-bot.persist.config;

import org.hibernate.cfg.Environment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by giga on 1/6/18.
 */
public class PersistConfigBuilder {

    PersistConfig settings = new PersistConfig();

    /*// Hibernate settings equivalent to hibernate.cfg.xml's properties
    Map<String, String> settings = new HashMap<>();
        settings.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
        settings.put(Environment.URL, "jdbc:mysql://127.0.0.1:33060/homestead");
        settings.put(Environment.USER, "homestead");
        settings.put(Environment.PASS, "secret");
        settings.put(Environment.DIALECT, "org.hibernate.dialect.PostgreSQL9Dialect");*/

    public void set(String key, String value) {
        if(value != null) {
            settings.put(key, value);
        }
    }

    public PersistConfigBuilder driver(String value) {
        set(Environment.DRIVER, value);

        return this;
    }

    public PersistConfigBuilder url(String value) {
        set(Environment.URL, value);

        return this;
    }

    public PersistConfigBuilder user(String value) {
        set(Environment.USER, value);

        return this;
    }

    public PersistConfigBuilder pass(String value) {
        set(Environment.PASS, value);

        return this;
    }

    public PersistConfigBuilder dialect(String value) {
        set(Environment.DIALECT, value);

        return this;
    }

    public PersistConfig build() {
        return new PersistConfig(settings);
    }
}
