package ge.arb-bot.daemon.util;

import ge.arb-bot.core.datatypes.util.FileSerializer;
import ge.arb-bot.daemon.exceptions.BookieScraperNotFoundException;
import ge.arb-bot.daemon.exceptions.CachedDataException;
import ge.arb-bot.daemon.fetch.BookieScraperRegistry;
import ge.arb-bot.scraper.BookieScraper;
import ge.arb-bot.scraper.datatypes.Category;
import ge.arb-bot.scraper.exceptions.ScraperException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by giga on 2/8/18.
 */
public class CachedData {

    protected static final String sufix = "_categories.dump";
    protected static final String directory = "shitbot-bookie-cache";

    public List<? extends Category> getCategories(String bookieName) throws CachedDataException {

        try {
            File file = getFile(bookieName);
            if(file.exists()) {
                return (List<? extends Category>) FileSerializer.fromFile(file.getAbsolutePath());
            }

            BookieScraper scraper = getBookieScraper(bookieName);

            List<? extends Category> categories = scraper.getFreshData();
            FileSerializer.toFile(file.getAbsolutePath(), categories);

            return categories;
        } catch (Exception e) {
            throw new CachedDataException(e);
        }
    }

    public File getFile(String bookieName) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        Path path = Paths.get(tmpDir, directory);
        String fileName = bookieName + sufix;

        return new File(path.toString(), fileName);
    }

    protected BookieScraper getBookieScraper(String bookieName) throws BookieScraperNotFoundException{
        return BookieScraperRegistry.getScraper(bookieName);
    }
}
