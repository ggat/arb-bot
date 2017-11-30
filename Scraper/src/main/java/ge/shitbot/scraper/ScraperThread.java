package ge.shitbot.scraper;

import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.exceptions.ScrapperException;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by giga on 11/30/17.
 */
public class ScraperThread implements Runnable {

    Semaphore semaphore;
    String name;
    String bookieName;
    BookieScraper scraper;
    Map<String, List<? extends Category>> categories;

    public ScraperThread(Semaphore semaphore, String name, String bookieName, BookieScraper scraper, Map<String, List<? extends Category>> categories) {
        this.semaphore = semaphore;
        this.name = name;
        this.bookieName = bookieName;
        this.scraper = scraper;
        this.categories = categories;

        new Thread(this).run();
    }

    public void run() {
        System.out.println("Starting " + name);
        try {
            // First, get a permit.

            System.out.println(name + " is waiting for a permit.");

            semaphore.acquire();

            System.out.println(name + " gets a permit.");
            // Now, access shared resource.

            try {
                categories.put(bookieName, scraper.getFreshData());
            } catch (ScrapperException e) {
                e.printStackTrace();
            }

        } catch (InterruptedException exc) {
            System.out.println(exc);
        }

        // Release the permit.
        System.out.println(name + " releases the permit.");
        semaphore.release();
    }
}
