package ge.shitbot.persist.services;

import ge.shitbot.persist.models.Person;
import ge.shitbot.persist.models.Ranking;
import ge.shitbot.persist.models.Skill;
import ge.shitbot.persist.util.SessionUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.IntSummaryStatistics;
import java.util.stream.Collectors;

/**
 * Created by giga on 12/8/17.
 */
public class HibernateRakingService implements RankingService {

    @Override
    public void addRanking(String subjectName, String observerName, String skillName, int rank){

        try(Session session = SessionUtil.getSession()) {
            Transaction transaction = session.beginTransaction();

            Person subject = savePerson(session, subjectName);
            Person observer = savePerson(session, observerName);
            Skill skill = saveSkill(session, skillName);

            Ranking ranking = new Ranking();
            ranking.setSubject(subject);
            ranking.setObserver(observer);
            ranking.setSkill(skill);
            ranking.setRanking(8);
            session.save(ranking);

            transaction.commit();
        }
    }

    @Override
    public int getRankingFor(String subject, String skill) {

        try(Session session = SessionUtil.getSession()) {
            Transaction transaction = session.beginTransaction();

            int average = getRankingFor(session, subject, skill);
            transaction.commit();

            return average;
        }
    }

    private int getRankingFor(Session session, String subject, String skill){
        Query<Ranking> query = session.createQuery("from Ranking r " +
                "where r.subject.name=:name " +
                "and r.skill.name=:skill", Ranking.class);

        query.setParameter("name", subject);
        query.setParameter("skill", skill);

        IntSummaryStatistics statistics = query.list().stream().collect(Collectors.summarizingInt(Ranking::getRanking));

        return (int) statistics.getAverage();
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
}
