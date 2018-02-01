package ge.shitbot.scraper;

import ge.shitbot.core.datatypes.OddType;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import ge.shitbot.scraper.exceptions.ScraperException;

import java.util.*;

import static org.junit.Assert.assertEquals;
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

    protected Set<OddType> getContainingOddTypes(List<? extends Category> categories) {

        Set<OddType> oddTypes = new HashSet<>();

        for (Category category : categories) {
            List<? extends Category> subCategories = category.getSubCategories();
            if(subCategories.size() == 0) continue;

            for (Category subCategory : category.getSubCategories()) {
                List<Event> events = subCategory.getEvents();

                if(events.size() == 0 ) continue;

                for (Event event : events) {
                    for(Map.Entry<OddType, Double> oddType : event.getOdds().entrySet()) {
                        oddTypes.remove(oddType.getKey());
                        oddTypes.add(oddType.getKey());
                    }
                }
            }
        }

        return oddTypes;
    }

    public void testOddScrap(BookieScraper scraper) throws ScraperException {
        int totalOddsParsed = countOdds(scraper.getFreshData());

        assertTrue("Total odds are not greater than 0", 0 < totalOddsParsed);
    }

    public void testAllOddTypesAreParsed(BookieScraper scraper) throws ScraperException {
        Set<OddType> oddTypes = getContainingOddTypes(scraper.getFreshData());

        assertEquals(10, oddTypes.size());
    }
}
