package ge.shitbot.persist.tests;

import ge.shitbot.persist.services.HibernateRakingService;
import ge.shitbot.persist.services.RankingService;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Created by giga on 12/8/17.
 */
public class AddRankingTest {

    RankingService rankingService = new HibernateRakingService();

    @Test
    public void addRanking(){
        rankingService.addRanking("J. C. Smell", "Drew Lombardo", "Mule", 8);
        assertEquals(rankingService.getRankingFor("J. C. Smell", "Mule"), 8);
    }
}
