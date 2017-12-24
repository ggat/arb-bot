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
public final class Collector implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(Collector.class);

    protected static List<ScraperThread> scraperThreads = new ArrayList<>();
    protected static ScraperThread.SharedData data = new ScraperThread.SharedData();
    protected static Object lock = new Object();
    protected static long scrapingInterval = 5;
    protected static List<DataUpdateHandler> updateHandlers = new ArrayList<>();
    protected static Thread thread;

    public static long getScrapingInterval() {
        return scrapingInterval;
    }

    public static void setScrapingInterval(long scrapingInterval) {
        Collector.scrapingInterval = scrapingInterval;
    }

    public void run() {
        Collector.action();
    }

    public static void start() {
        thread = new Thread( new Collector(),"Collector Thread");
        thread.start();
    }

    public static void stop() {
        Collector.stopAction();
    }

    public static void action() {

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

                    System.out.println("TMPL");

                    synchronized (data) {

                        logger.info("LastUpdatedKey: {}", data.lastUpdatedKey());

                        //FIXME: Importan if something fails here i.e. RuntimeException.
                        // Collector thread fails, while other threads continue to execute.
                        List<? extends Category> updatedItem = data.getLastUpdated();
                        String target = data.lastUpdatedKey();
                        handleDataUpdate(updatedItem, target);
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



    public static void stopAction() {
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


    //TODO: Look at me thread safety!
    //FIXME: Is it enough to clone updateItem here to defend thread safety.
    protected static void handleDataUpdate(List<? extends Category> updatedItem, String target) {

        for (DataUpdateHandler handler : updateHandlers) {
            handler.handle(new DataUpdateEvent(new ArrayList<Category>(updatedItem), target));
        }

        logger.info("Data {}", data.size());
    }

    public static void addUpdateEventHandler(DataUpdateHandler handler) {

        updateHandlers.add(handler);

        logger.info("Updated handles {}", updateHandlers.size());
    }
}
