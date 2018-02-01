package ge.shitbot.scraper.bookies;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ge.shitbot.core.datatypes.OddType;
import ge.shitbot.scraper.datatypes.Event;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by giga on 11/12/17.
 */
public class CrocoBetScraper extends AbstractEuropeCrocoScraper {

    // Game types we currently take cate of
    protected String[] engGameTypes = {"1X2 ①", "Double Chance  ①", "Under/Over 2.5 goals ①", "Both teams to score  ①"};

    //TODO: Enable after gameTypes for other langs are added, also we need to get langUsed from somwhere.
    //if(Language.ENGLISH == langUsed) {
    List<String> list = new ArrayList<>(Arrays.asList(engGameTypes));
    //}

    public CrocoBetScraper() {
        setLogger(LoggerFactory.getLogger(CrocoBetScraper.class));
    }

    @Override
    public String getSearchUrl(){
        return "https://www.crocobet.com/rest/market/categories";
    }

    @Override
    public String getEventsUrl(Long subCategoryId) {

        return "https://www.crocobet.com/rest/market/categories/multi/"+ subCategoryId +"/events";
    }

    protected String[] getList() {

        String[] list = {"1X2 *", "Double Chance *", "Under/Over 2.5 goals *", "Both teams to score *"};

        return list;
    }

    @Override
    protected ArrayList<LocalEvent> mapLocalEvents(String data) throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(data, new TypeReference<ArrayList<CrocoEvent>>() {});
    }

    protected static class CrocoEvent extends LocalEvent {
        @JsonDeserialize(using = CrocoEventOddsDeserializer.class)
        @Override
        public void setOdds(Map<OddType, Double> odds) {
            super.setOdds(odds);
        }
    }

    protected static class CrocoEventOddsDeserializer extends LocalEventOddsDeserializer {

        @Override
        public Map<String, OddType> getOddNameMapping() {

            Map<String, OddType> oddNameMapping = new HashMap<>();
            oddNameMapping.put("1", OddType._1);
            oddNameMapping.put("X", OddType._X);
            oddNameMapping.put("2", OddType._2);
            oddNameMapping.put("1X", OddType._1X);
            oddNameMapping.put("12", OddType._12);
            oddNameMapping.put("X2", OddType._X2);
            oddNameMapping.put("Under 2.5", OddType._UNDER_25);
            oddNameMapping.put("Over 2.5", OddType._OVER_25);

            //NOTE: For some reason somtimes these odd types have two spaces between instead of one.
            oddNameMapping.put("Under  2.5", OddType._UNDER_25);
            oddNameMapping.put("Over  2.5", OddType._OVER_25);
            oddNameMapping.put("Yes", OddType._YES);
            oddNameMapping.put("No", OddType._NO);

            return oddNameMapping;
        }

        @Override
        public String[] getEngGameTypes() {
            String[] engGameTypes = {"1X2 ①", "Double Chance  ①", "Under/Over 2.5 goals ①", "Both teams to score  ①"};
            return engGameTypes;
        }
    }

    @Override
    public String getOutRightsString() {
        return "Outright";
    }
}
