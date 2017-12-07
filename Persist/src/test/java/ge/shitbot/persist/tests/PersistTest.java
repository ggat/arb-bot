package ge.shitbot.persist.tests;

import ge.shitbot.persist.Event;
import junit.framework.TestCase;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * Created by giga on 12/7/17.
 */
public class PersistTest {

    SessionFactory sessionFactory;

    @Before
    public void setUp() throws Exception {

        // A SessionFactory is set up once for an application!
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure() // configures settings from hibernate.cfg.xml
                .build();
        try {
            sessionFactory = new MetadataSources( registry ).buildMetadata().buildSessionFactory();
        }
        catch (Exception e) {
            // The registry would be destroyed by the SessionFactory, but we had trouble building the SessionFactory
            // so destroy it manually.
            StandardServiceRegistryBuilder.destroy( registry );
            e.printStackTrace();
        }

        System.out.println("Factory");

    }

    @Test
    public void testSaveEvents() {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(new Event("BMW"));
        session.save(new Event("Honda"));
        session.getTransaction().commit();

        session.beginTransaction();
        List<Event> eventList = session.createQuery("FROM Event", Event.class).list();
        session.getTransaction().commit();

        assert eventList.size() > 1;

        session.close();
    }
}
