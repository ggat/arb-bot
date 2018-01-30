package ge.shitbot.scraper;

import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import ge.shitbot.scraper.exceptions.ScraperException;

import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by giga on 1/30/18.
 */
public abstract class ScraperTest {

    protected int countOdds(List<? extends Category> categories) {
        int oddsParsed = 0;
        for (Category category : categories) {
            List<? extends Category> subCategories = category.getSubCategories();
            if(subCategories.size() == 0) continue;

            for (Category subCategory : category.getSubCategories()) {
                List<Event> events = subCategory.getEvents();

                if(events.size() == 0 ) continue;

                for (Event event : events) {
                    oddsParsed += event.getOdds().size();
                }
            }
        }

        return oddsParsed;
    }

    public void testOddScrap(BookieScraper scraper) throws ScraperException {
        int totalOddsParsed = countOdds(scraper.getFreshData());

        assertTrue("Total odds are not greater than 0", 0 < totalOddsParsed);
    }
}
