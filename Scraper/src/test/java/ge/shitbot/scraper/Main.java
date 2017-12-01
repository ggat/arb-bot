package ge.shitbot.scraper;

import ge.shitbot.scraper.bookies.*;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import ge.shitbot.scraper.exceptions.ScrapperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by giga on 11/21/17.
 */
public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws ScrapperException {

        BookieScraper scraper = new EuropeBetScraper();

        //BetLiveScraper scraper = new BetLiveScraper();

        //Map<Long, List<Event>> events = scraper.getAllEventsForSport();

        //try {

        logger.warn("My own logger.");

            //scraper.getAllEventsForSport();

            //List<? extends Category> result =  scraper.getFreshData();

            Map<String, List<? extends Category>> allCategories = BulkRunner.getCategories();

            System.out.println("asdasd");

            /*Event firstEvent = result.get(0).getSubCategories().get(0).getEvents().get(0);

            System.out.println("First event sideOne: " + firstEvent.getSideOne());
            System.out.println("First event getSideTwo: " + firstEvent.getSideTwo());
            System.out.println("First event subCategory: " + firstEvent.getCategory().getName());
            System.out.println("First event category: " + firstEvent.getCategory().getParent().getName());*/

        /*} catch (ScrapperException e) {
            e.printStackTrace();
        }*/
    }
}
