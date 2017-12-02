package ge.shitbot.daemon.fetch;

import ge.shitbot.scraper.BookieScraper;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.exceptions.ScraperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by giga on 11/30/17.
 */
public class ScraperThread implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ScraperThread.class);

    protected Semaphore semaphore;
    protected String name;
    protected String bookieName;
    protected BookieScraper scraper;
    protected long fetchInterval = 60;
    private long successFetchCount = 0;

    //Thread specific
    private Thread thread;
    //private boolean mustRun;

    Map<String, List<? extends Category>> categories;

    public ScraperThread(Semaphore semaphore, String name, String bookieName, BookieScraper scraper,
                         Map<String, List<? extends Category>> categories, long fetchInterval) {
        this.semaphore = semaphore;
        this.name = name;
        this.bookieName = bookieName;
        this.scraper = scraper;
        this.categories = categories;
        this.fetchInterval = fetchInterval;
    }

    public void start() {
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void terminate() throws InterruptedException {
        this.thread.join();
    }

    public void run() {

        logger.info("Starting thread: {}", name);

        while (true) {
            try {

                List<? extends Category> data = fetchIt();

                // First, get a permit.

                logger.info("{} is waiting for a permit.", name);

                semaphore.acquire();

                logger.info("{} gets a permit.", name);
                // Now, access shared resource.

                categories.put(bookieName, data);

            } catch (InterruptedException exc) {
                logger.debug("Interrupted first.");
                Thread.currentThread().interrupt();
            }

            // Release the permit.
            logger.info("{} releases the permit.", name);
            semaphore.release();
        }
    }

    private List<? extends Category> fetchIt() {

        logger.info("Fetching #{} now.", successFetchCount);

        try {

            logger.info("Putting fetch #{} data.", successFetchCount);

            //categories.put(bookieName, scraper.getFreshData());
            List<? extends Category> freshData = scraper.getFreshData();
            successFetchCount++;
            return freshData;

        } catch (ScraperException e) {

            logger.info("Fetch #{} failed, will retry within {} seconds..", successFetchCount,
                    fetchInterval / 1000 );

            //Wait little bit after fail.
            try {
                Thread.sleep(fetchInterval);
            } catch (InterruptedException e1) {

                logger.debug("Interrupted second.");
                Thread.currentThread().interrupt();
            }

            logger.info("Retrying fetch #{} now.", successFetchCount);
            //Try to fetch it again.
            return fetchIt();
        }
    }

    public String getName() {
        return name;
    }

    public String getBookieName() {
        return bookieName;
    }
}
