package ge.arb-bot.scraper;

import ge.arb-bot.scraper.bookies.LiderBetScraper;
import ge.arb-bot.scraper.exceptions.ScraperException;
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

    @Test
    public void testAllOddTypesAreParsed() throws ScraperException {
        super.testAllOddTypesAreParsed(new LiderBetScraper());
    }
}
