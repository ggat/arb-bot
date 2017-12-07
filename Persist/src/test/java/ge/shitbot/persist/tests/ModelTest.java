package ge.shitbot.persist.tests;

import ge.shitbot.persist.models.Person;
import ge.shitbot.persist.models.Ranking;
import ge.shitbot.persist.models.Skill;
import org.junit.Test;

/**
 * Created by giga on 12/7/17.
 */
public class ModelTest {

    @Test
    public void testModelCreation() {
        Person subject = new Person();
        subject.setName("J. C. Smell");

        Person observer = new Person();
        observer.setName("Drew Lombardo");

        Skill skill = new Skill();
        skill.setName("Java");

        Ranking ranking = new Ranking();
        ranking.setSubject(subject);
        ranking.setObserver(observer);
        ranking.setSkill(skill);
        ranking.setRanking(8);

        System.out.println(ranking);
    }
}
