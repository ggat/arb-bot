package ge.shitbot.scraper.bookies;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * Created by giga on 11/12/17.
 */
public class EuropeBetScraper extends AbstractEuropeCrocoScraper {

    public EuropeBetScraper() {
        setLogger(LoggerFactory.getLogger(EuropeBetScraper.class));
    }

    //String searchUrl = "https://sport2.europebet.com/rest/market/categories?_=1511257613714";
    //String searchUrl = ;

    @Override
    public String getSearchUrl(){
        return "https://sport2.europebet.com/rest/market/categories?_=" + (new Random().nextInt(999999999) + 1);
    }

    @Override
    public String getEventsUrl(Integer subCategoryId) {
        return "https://sport2.europebet.com/rest/market/categories/"+ subCategoryId +"/events?_=1511269825840";
    }
}
