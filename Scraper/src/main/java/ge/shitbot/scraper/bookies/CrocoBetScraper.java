package ge.shitbot.scraper.bookies;

import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Created by giga on 11/12/17.
 */
public class CrocoBetScraper extends AbstractEuropeCrocoScraper {

    public CrocoBetScraper() {
        setLogger(LoggerFactory.getLogger(CrocoBetScraper.class));
    }

    @Override
    public String getSearchUrl(){
        return "https://www.crocobet.com/rest/market/categories";
    }

    @Override
    public String getEventsUrl(Integer subCategoryId) {

        return "https://www.crocobet.com/rest/market/categories/multi/"+ subCategoryId +"/events";
    }
}
