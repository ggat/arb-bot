package ge.shitbot.daemon.fetch;

import ge.shitbot.daemon.exceptions.BookieScraperNotFoundException;
import ge.shitbot.hardcode.BookieNames;
import ge.shitbot.scraper.BookieScraper;
import ge.shitbot.scraper.datatypes.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Created by giga on 11/30/17.
 */
public final class Collector {

    private static Logger logger = LoggerFactory.getLogger(Collector.class);

    protected static List<ScraperThread> scraperThreads = new ArrayList<>();
    protected static ScraperThread.SharedData data = new ScraperThread.SharedData();
    protected static Object lock = new Object();
    protected static long scrapingInterval = 30;
    protected static List<DataUpdateHandler> updateHandlers = new ArrayList<>();

    public static long getScrapingInterval() {
        return scrapingInterval;
    }

    public static void setScrapingInterval(long scrapingInterval) {
        Collector.scrapingInterval = scrapingInterval;
    }

    public static void start() {

        long interval = scrapingInterval * 1000;
        List<String> bookieNames = BookieNames.asList();

        for (int num = 0; num < bookieNames.size(); num++) {

            String bookieName = bookieNames.get(num);

            BookieScraper scraper = null;
            try {
                scraper = BookieScraperRegistry.getScraper(bookieName);
            } catch (BookieScraperNotFoundException e) {
                logger.warn("Scraper not found for bookie {} skipping.", bookieName);
                continue;
            }

            ScraperThread scraperThread = new ScraperThread("ScraperThread " + num,
                    bookieName, scraper, data, interval, lock);
            scraperThreads.add(scraperThread);

            logger.info("Created scraper thread for bookie {} and starting it..", bookieName);
            scraperThread.start();
        }

        while (true) {

            try {

                logger.debug("Waiting for data to be added.");

                synchronized (lock) {
                    lock.wait();

                    synchronized (data) {

                        logger.info("LastUpdatedKey: {}", data.lastUpdatedKey());

                        List<? extends Category> updatedItem = data.getLastUpdated();
                        handleDataUpdate(updatedItem);
                    }
                }
            } catch (InterruptedException e) {
                logger.warn("Collector thread interrupted.");
                Thread.currentThread().interrupt();
            }
        }
    }

    public static boolean isRunning() {
        return scraperThreads.size() > 0;
    }

    public static Map<String, List<? extends Category>> getData() {
        return new HashMap<>(data);
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

    protected static void handleDataUpdate(List<? extends Category> updatedItem) {


        for (DataUpdateHandler handler : updateHandlers) {
            handler.handle(new DataUpdateEvent(updatedItem));
        }

        logger.info("Data {}", data.size());
    }

    public static void addUpdateEventHandler(DataUpdateHandler handler) {

        updateHandlers.add(handler);

        logger.info("Updated handles {}", updateHandlers.size());
    }
}
