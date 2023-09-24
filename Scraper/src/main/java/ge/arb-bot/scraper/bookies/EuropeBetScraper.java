package ge.arb-bot.scraper.bookies;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ge.arb-bot.core.datatypes.OddType;
import org.slf4j.LoggerFactory;
import java.util.*;

/**
 * Created by giga on 11/12/17.
 */
public class EuropeBetScraper extends AbstractEuropeCrocoScraper {

    // Game types we currently take cate of
    protected String[] engGameTypes = {"1X2 *", "Double Chance *", "Under/Over 2.5 goals *", "Both teams to score *"};

    //TODO: Enable after gameTypes for other langs are added, also we need to get langUsed from somwhere.
    //if(Language.ENGLISH == langUsed) {
    List<String> list = new ArrayList<>(Arrays.asList(engGameTypes));
    //}


    public EuropeBetScraper() {
        setLogger(LoggerFactory.getLogger(EuropeBetScraper.class));
    }

    //String searchUrl = "https://sport2.europebet.com/rest/market/categories?_=1511257613714";
    //String searchUrl = ;

    protected String[] getList() {

        String[] list = {"1X2 *", "Double Chance *", "Under/Over 2.5 goals *", "Both teams to score *"};

        return list;
    }

    @Override
    public String getSearchUrl(){
        return "https://sport2.europebet.com/rest/market/categories?_=" + (new Random().nextInt(999999999) + 1);
    }

    @Override
    public String getEventsUrl(Long subCategoryId) {
        return "https://sport2.europebet.com/rest/market/categories/"+ subCategoryId +"/events?_=1511269825840";
    }

    @Override
    protected ArrayList<LocalEvent> mapLocalEvents(String data) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(data, new TypeReference<ArrayList<EuropeEvent>>() {});
    }

    protected static class EuropeEvent extends LocalEvent {

        @JsonDeserialize(using = EuropeEventOddsDeserializer.class)
        @Override
        public void setOdds(Map<OddType, Double> odds) {
            super.setOdds(odds);
        }
    }

    protected static class EuropeEventOddsDeserializer extends LocalEventOddsDeserializer {

        @Override
        public Map<String, OddType> getOddNameMapping() {

            Map<String, OddType> oddNameMapping = new HashMap<>();
            oddNameMapping.put("1", OddType._1);
            oddNameMapping.put("X", OddType._X);
            oddNameMapping.put("2", OddType._2);
            oddNameMapping.put("1/X", OddType._1X);
            oddNameMapping.put("1/2", OddType._12);
            oddNameMapping.put("X/2", OddType._X2);
            oddNameMapping.put("Under", OddType._UNDER_25);
            oddNameMapping.put("Over", OddType._OVER_25);
            oddNameMapping.put("Yes", OddType._YES);
            oddNameMapping.put("No", OddType._NO);

            return oddNameMapping;
        }

        @Override
        public String[] getEngGameTypes() {
            String[] engGameTypes = {"1X2 *", "Double Chance *", "Under/Over 2.5 goals *", "Both teams to score *"};
            return engGameTypes;
        }
    }

    @Override
    public String getOutRightsString() {
        return "Outrights";
    }
}
