package ge.shitbot.scraper;

import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.exceptions.ScrapperException;

import java.util.List;

/**
 * Created by giga on 11/27/17.
 */
public interface BookieScraper {

    List<? extends Category> getFreshData() throws ScrapperException;
}
