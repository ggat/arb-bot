package ge.arb-bot.persist.tests;

import ge.arb-bot.persist.models.Person;
import ge.arb-bot.persist.models.Ranking;
import ge.arb-bot.persist.models.Skill;
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
