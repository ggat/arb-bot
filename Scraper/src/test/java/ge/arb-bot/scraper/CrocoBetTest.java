package ge.arb-bot.scraper;

import ge.arb-bot.scraper.bookies.CrocoBetScraper;
import ge.arb-bot.scraper.exceptions.ScraperException;
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

    @Test
    public void testAllOddTypesAreParsed() throws ScraperException {
        super.testAllOddTypesAreParsed(new CrocoBetScraper());
    }
}
