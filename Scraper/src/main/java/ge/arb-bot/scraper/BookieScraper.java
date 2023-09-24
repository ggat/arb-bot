package ge.arb-bot.scraper;

import ge.arb-bot.scraper.datatypes.Category;
import ge.arb-bot.scraper.exceptions.ScraperException;

import java.util.List;

/**
 * Created by giga on 11/27/17.
 */
public interface BookieScraper {

    List<? extends Category> getFreshData() throws ScraperException;
}
