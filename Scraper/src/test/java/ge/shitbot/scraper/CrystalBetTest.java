package ge.shitbot.scraper;

import ge.shitbot.scraper.bookies.CrystalBetScraper;
import ge.shitbot.scraper.bookies.LiderBetScraper;
import ge.shitbot.scraper.exceptions.ScraperException;
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
