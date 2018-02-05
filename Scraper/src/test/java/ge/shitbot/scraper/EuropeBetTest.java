package ge.shitbot.scraper;

import ge.shitbot.scraper.bookies.EuropeBetScraper;
import ge.shitbot.scraper.exceptions.ScraperException;
import org.junit.Test;

/**
 * Created by giga on 1/30/18.
 */
public class EuropeBetTest extends ScraperTest {

    @Test
    public void testOddScrap() throws ScraperException {
        super.testOddScrap(new EuropeBetScraper());
    }

    @Test
    public void testAllOddTypesAreParsed() throws ScraperException {
        super.testAllOddTypesAreParsed(new EuropeBetScraper());
    }
}
