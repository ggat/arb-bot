package ge.shitbot.persist.tests;

import ge.shitbot.persist.models.Person;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by giga on 12/7/17.
 */
public class PersonTest {

    SessionFactory factory;

    @Before
    public void setup() {
        StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure()
                .build();
        factory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    @Test
    public void testSavePerson() {

        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            Person person = new Person();
            person.setName("j. C. Smell");

            session.save(person);

            tx.commit();
        }
    }

    @Test
    public void testReadPerson() {

        try (Session session = factory.openSession()) {
            Person person = findPerson(session, "j. C. Smell");

            System.out.println(person);

            assert person != null;
        }
    }

    public Person findPerson(Session session, String name) {
        Query<Person> query = session.createQuery("from Person p where p.name=:name", Person.class);
        query.setParameter("name", name);
        Person person = query.setMaxResults(1).uniqueResult();

        return person;
    }
}
