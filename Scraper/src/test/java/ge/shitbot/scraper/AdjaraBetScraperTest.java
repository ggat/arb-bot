package ge.shitbot.scraper;

import ge.shitbot.scraper.bookies.AdjaraBetScraper;
import ge.shitbot.scraper.exceptions.ScraperException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by giga on 1/30/18.
 */
public class AdjaraBetScraperTest extends ScraperTest {

    @Test
    public void testOddScrap() throws ScraperException {
        super.testOddScrap(new AdjaraBetScraper());
    }

    @Test
    public void testAllOddTypesAreParsed() throws ScraperException {
        super.testAllOddTypesAreParsed(new AdjaraBetScraper());
    }
}
