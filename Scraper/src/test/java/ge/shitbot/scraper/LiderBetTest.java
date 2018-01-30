package ge.shitbot.scraper;

import ge.shitbot.scraper.bookies.LiderBetScraper;
import ge.shitbot.scraper.exceptions.ScraperException;
import org.junit.Test;

import java.util.List;

/**
 * Created by giga on 1/30/18.
 */
public class LiderBetTest extends ScraperTest {

    @Test
    public void testOddScrap() throws ScraperException {
        super.testOddScrap(new LiderBetScraper());
    }
}
