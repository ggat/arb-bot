package ge.shitbot.scraper;

import ge.shitbot.scraper.bookies.*;
import ge.shitbot.scraper.datatypes.Category;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * Created by giga on 11/30/17.
 */
public class BulkRunner {

    protected static Semaphore semaphore = new Semaphore(1);

    public static Map<String, List<? extends Category>> getCategories() {

        Map<String, List<? extends Category>> result = new LinkedHashMap<>();

        new ScraperThread(semaphore, "Thread 01", "Adjara", new AdjaraBetScraper(), result);
        new ScraperThread(semaphore, "Thread 02", "Lider", new LiderBetScraper(), result);
        new ScraperThread(semaphore, "Thread 03", "Europe", new EuropeBetScraper(), result);
        new ScraperThread(semaphore, "Thread 04", "Croco", new CrocoBetScraper(), result);
        new ScraperThread(semaphore, "Thread 05", "Crystal", new CrystalBetScraper(), result);
        //new ScraperThread(semaphore, "Thread 06", "Betlive", new BetLiveScraper(), result);

        return result;

    }
}
