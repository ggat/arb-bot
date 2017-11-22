package ge.shitbot.scraper;

import ge.shitbot.scraper.bookies.AdjaraBetScraper;
import ge.shitbot.scraper.bookies.CrocoBetScraper;
import ge.shitbot.scraper.bookies.EuropeBetScraper;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.exceptions.ScrapperException;

import java.util.List;

/**
 * Created by giga on 11/21/17.
 */
public class Main {

    public static void main(String[] args) {
        AdjaraBetScraper scraper = new AdjaraBetScraper();

        try {
            List<? extends Category> result =  scraper.getFreshData();

            System.out.println("asdasd");

            /*Event firstEvent = result.get(0).getSubCategories().get(0).getEvents().get(0);

            System.out.println("First event sideOne: " + firstEvent.getSideOne());
            System.out.println("First event getSideTwo: " + firstEvent.getSideTwo());
            System.out.println("First event subCategory: " + firstEvent.getCategory().getName());
            System.out.println("First event category: " + firstEvent.getCategory().getParent().getName());*/

        } catch (ScrapperException e) {
            e.printStackTrace();
        }
    }
}
