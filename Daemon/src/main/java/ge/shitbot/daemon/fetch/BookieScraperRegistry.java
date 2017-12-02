package ge.shitbot.daemon.fetch;

import ge.shitbot.daemon.exceptions.BookieScraperNotFoundException;
import ge.shitbot.hardcode.BookieNames;
import ge.shitbot.scraper.BookieScraper;
import ge.shitbot.scraper.bookies.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by giga on 12/2/17.
 */
public class BookieScraperRegistry {

    public static BookieScraper getScraper(String bookieName) throws BookieScraperNotFoundException {

        BookieScraper created;

        switch (bookieName) {
            case BookieNames.AJARA_BET:
                created = new AdjaraBetScraper();
                break;
            case BookieNames.BET_LIVE:
                created = new BetLiveScraper();
                break;
            case BookieNames.CRYSTAL_BET:
                created = new CrystalBetScraper();
                break;
            case BookieNames.EUROPE_BET:
                created = new EuropeBetScraper();
                break;
            case BookieNames.LIDER_BET:
                created = new LiderBetScraper();
                break;
            case BookieNames.CROCO_BET:
                created = new CrocoBetScraper();
                break;
            default:
                throw new BookieScraperNotFoundException("Scraper not found for bookie with name " + bookieName);
        }

        return created;
    }
}
