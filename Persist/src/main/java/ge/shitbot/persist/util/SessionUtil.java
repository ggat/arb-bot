package ge.shitbot.persist.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by giga on 12/8/17.
 */
public class SessionUtil {

    //Logger logger = LoggerFactory.getLogger(SessionUtil.class);

    private static final SessionUtil instance = new SessionUtil();
    private final SessionFactory factory;
    private static final String CONFIG_NAME = "/configuration.properties";
    private static Session session;

    private SessionUtil() {

        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static Session getSession() {
        return getInstance().factory.openSession();
    }

    public static Session getSingletonSession() {
        if (session == null) {
            session = getSession();
        }

        return session;
    }

    private static SessionUtil getInstance() {
        return instance;
    }
}
