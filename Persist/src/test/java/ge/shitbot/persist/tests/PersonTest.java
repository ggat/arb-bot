package ge.shitbot.persist.tests;

import ge.shitbot.persist.models.Person;
import ge.shitbot.persist.models.Ranking;
import ge.shitbot.persist.models.Skill;
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
            savePerson(session, "j. C. Smell");
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

    public Person savePerson(Session session, String name) {
        Person person = findPerson(session, name);

        if(person == null) {
            person = new Person();
            person.setName(name);
            session.save(person);
        }

        return person;
    }

    public Skill findSkill(Session session, String name) {
        Query<Skill> query = session.createQuery("from Skill s where s.name=:name", Skill.class);
        query.setParameter("name", name);
        return query.setMaxResults(1).uniqueResult();
    }

    public Skill saveSkill(Session session, String name) {
        Skill skill = findSkill(session, name);

        if(skill == null) {
            skill = new Skill();
            skill.setName(name);
            session.save(skill);
        }

        return skill;
    }

    @Test
    public void testSaveSkill() {
        try (Session session = factory.openSession()) {
            Transaction tx = session.beginTransaction();
            saveSkill(session, "Java");
            tx.commit();
        }
    }

    @Test
    public void testSaveRanking() {
        try(Session session = factory.openSession()) {
            Transaction transaction = session.beginTransaction();

            Person subject = savePerson(session, "Giga Gatenashvili");
            Person observer = savePerson(session, "Alex Kazaziani");
            Skill skill = saveSkill(session, "Java");

            Ranking ranking = new Ranking();
            ranking.setSubject(subject);
            ranking.setObserver(observer);
            ranking.setSkill(skill);
            ranking.setRanking(8);
            session.save(ranking);

            transaction.commit();

        }
    }
}
