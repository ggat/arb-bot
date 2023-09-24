package ge.arb-bot.scraper;

import ge.arb-bot.scraper.bookies.CrystalBetScraper;
import ge.arb-bot.scraper.bookies.LiderBetScraper;
import ge.arb-bot.scraper.exceptions.ScraperException;
import org.junit.Test;

/**
 * Created by giga on 1/30/18.
 */
public class CrystalBetTest extends ScraperTest {

    @Test
    public void testOddScrap() throws ScraperException {
        super.testOddScrap(new CrystalBetScraper());
    }

    @Test
    public void testAllOddTypesAreParsed() throws ScraperException {
        super.testAllOddTypesAreParsed(new CrystalBetScraper());
    }
}
