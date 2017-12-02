package ge.shitbot.daemon.fetch;

import ge.shitbot.daemon.exceptions.BookieScraperNotFoundException;
import ge.shitbot.hardcode.BookieNames;
import ge.shitbot.scraper.BookieScraper;
import ge.shitbot.scraper.bookies.*;
import ge.shitbot.scraper.datatypes.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by giga on 11/30/17.
 */
public final class Fetcher {

    private static Logger logger = LoggerFactory.getLogger(Fetcher.class);

    protected static Semaphore semaphore = new Semaphore(1);
    protected static List<ScraperThread> scraperThreads = new ArrayList<>();
    protected static Map<String, List<? extends Category>> data = new LinkedHashMap<>();

    public static Map<String, List<? extends Category>> start() {

        long interval = 30 * 1000;

        List<String> bookieNames = BookieNames.asList();

        for (int num = 0; num < bookieNames.size(); num++ ) {

            String bookieName = bookieNames.get(num);

            BookieScraper scraper = null;
            try {
                scraper = BookieScraperRegistry.getScraper(bookieName);
            } catch (BookieScraperNotFoundException e) {
                logger.warn("Scraper not found for bookie {} skipping.", bookieName);
                continue;
            }

            ScraperThread scraperThread = new ScraperThread(semaphore, "ScraperThread " + num,
                    bookieName, scraper, data, interval);
            scraperThreads.add(scraperThread);

            logger.info("Created scraper thread for bookie {} and starting it..", bookieName);
            scraperThread.start();
        }

        return data;
    }

    public static boolean isRunning() {
        return scraperThreads.size() > 0;
    }

    public static Map<String, List<? extends Category>> getData() {
        return data;
    }

    public static void stop() {
        logger.info("Stop fetching data going to terminate all scraper threads.");

        while (scraperThreads.size() > 0) {
            ScraperThread scraperThread = scraperThreads.remove(0);

            try {
                logger.info("Terminating thread {} of bookie {}", scraperThread.getName(),
                        scraperThread.getBookieName());

                scraperThread.terminate();
            } catch (InterruptedException e) {

                logger.warn("Interruption while trying to terminate thread {} of bookie {}",
                        scraperThread.getName(), scraperThread.getBookieName());

                Thread.currentThread().interrupt();
            }
        }

        logger.info("All scraper threads terminated successfully.");
    }
}
