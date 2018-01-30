package ge.shitbot.scraper;

import ge.shitbot.scraper.bookies.CrocoBetScraper;
import ge.shitbot.scraper.exceptions.ScraperException;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by giga on 1/30/18.
 */
public class CrocoBetTest extends ScraperTest {

    @Test
    public void testOddScrap() throws ScraperException {
        super.testOddScrap(new CrocoBetScraper());
    }
}
