package ge.shitbot.daemon;

import ge.shitbot.daemon.exceptions.BookieScraperNotFoundException;
import ge.shitbot.daemon.util.CachedData;
import ge.shitbot.scraper.BookieScraper;
import ge.shitbot.scraper.datatypes.Category;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by giga on 12/7/17.
 */
public class CachedDataTest {

    protected static class LocalCachedData extends CachedData {

        @Override
        protected BookieScraper getBookieScraper(String bookieName) throws BookieScraperNotFoundException{
            return new DumbScraper();
        }
    }

    protected static class DumbScraper implements BookieScraper {

        public List<? extends Category> getFreshData() {
            List<Category> categories = new ArrayList<>();

            categories.add(new Category("Georgia", 1L));
            categories.add(new Category("Ukraine", 2L));

            return categories;
        }
    }

    @Test
    public void testGetCachedData() throws Exception {

        LocalCachedData cachedData = new LocalCachedData();

        String bookieName = "DumbBookieName";
        File bookieFile =  cachedData.getFile(bookieName);

        if(bookieFile.exists()) {
            if(! bookieFile.delete() ) {
                throw new Exception("Could not delete bookie file");
            }
        }

        List<? extends Category> categories = cachedData.getCategories(bookieName);

        assertArrayEquals(categories.toArray(), new DumbScraper().getFreshData().toArray());
    }
}
