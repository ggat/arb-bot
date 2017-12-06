package ge.shitbot.daemon.fetch;

import ge.shitbot.scraper.BookieScraper;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.exceptions.ScraperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by giga on 11/30/17.
 */
public class ScraperThread implements Runnable {

    private static Logger logger = LoggerFactory.getLogger(ScraperThread.class);

    protected String name;
    protected String bookieName;
    protected BookieScraper scraper;
    protected long fetchInterval = 60;
    private long successFetchCount = 0;
    protected Object localLock;

    //Thread specific
    private Thread thread;
    //private boolean mustRun;

    SharedData categories;

    public static class SharedData extends HashMap<String, List<? extends Category>> {
        private String lastUpdated;

        public String lastUpdatedKey() {
            return lastUpdated;
        }

        public List<? extends Category> getLastUpdated() {
            return lastUpdated == null ? null : this.get(lastUpdated);
        }

        @Override
        public List<? extends Category> put(String key, List<? extends Category> value) {
            List<? extends Category> result = super.put(key, value);

            lastUpdated = key;

            return result;
        }

    }

    public ScraperThread(String name, String bookieName, BookieScraper scraper,
                         SharedData categories, long fetchInterval, Object lock) {
        this.name = name;
        this.bookieName = bookieName;
        this.scraper = scraper;
        this.categories = categories;
        this.fetchInterval = fetchInterval;
        this.localLock = lock;
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

            List<? extends Category> data = fetchIt();
            synchronized (categories) {
                synchronized (localLock) {
                    categories.put(bookieName, data);
                    localLock.notifyAll();
                }
            }

            try {
                Thread.sleep(fetchInterval);
            } catch (InterruptedException e) {

                logger.warn("Interrupted thread {} ", name);
                Thread.currentThread().interrupt();
            }
        }
    }

    private List<? extends Category> fetchIt() {
        try {

        logger.info("Putting fetch #{} data.", successFetchCount);

        //categories.put(bookieName, scraper.getFreshData());
        List<? extends Category> freshData = scraper.getFreshData();
        //List<? extends Category> freshData = new ArrayList<>();
        successFetchCount++;
        return freshData;

        } catch (ScraperException e) {

            logger.info("Fetch #{} failed, will retry within {} seconds..", successFetchCount,
                    fetchInterval / 1000);

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
