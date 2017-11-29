package ge.shitbot.scraper.bookies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.node.ObjectNode;
import ge.shitbot.core.datatypes.OddType;
import ge.shitbot.core.datatypes.deserialize.AbstractDateDeserializer;
import ge.shitbot.core.datatypes.util.http.Http;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import ge.shitbot.scraper.exceptions.ScrapperException;
import ge.shitbot.scraper.exceptions.UncheckedScrapperException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by giga on 11/22/17.
 */
public class BetLiveScraper {

    private static Logger logger = LoggerFactory.getLogger(BetLiveScraper.class);

    protected final Long MAX_PAGE_COUNT = 11L;
    protected Long SOCCER_ID = 1L;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SportCountryCategory {
        public Long id;
        public Long orderPriority;
        public String name;
        public Long parentId;
        public Long categoryType;
        public Long countryId;
        public Object hasLeagueSubGroups;
        public Object showAsSubLeague;
        public Long eventCount;
        public List<CategoryContract> categoryContracts;
        public List<Object> eventContracts;
        public Object defaultMarketsForLeague;
        public Object marketDisplayTypeContracts;
        //public MarketRestrictions2 marketRestrictions;
        public Object key;
        public Long sportId;
        public Boolean isCategoryOnline;
        public Object parentLeagueId;
        public Boolean markAsNeutral;
        public Object livePriority;
        public Object categoryNameTranslates;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CategoryContract {
        public Long id;
        public Long orderPriority;
        public String name;
        public Object parentId;
        public Long categoryType;
        public Long countryId;
        public Object hasLeagueSubGroups;
        public Object showAsSubLeague;
        public Long eventCount;
        public List<Object> categoryContracts;
        public List<Object> eventContracts;
        public Object defaultMarketsForLeague;
        public Object marketDisplayTypeContracts;
        //public MarketRestrictions marketRestrictions;
        public Object key;
        public Object sportId;
        public Boolean isCategoryOnline;
        public Object parentLeagueId;
        public Boolean markAsNeutral;
        public Long livePriority;
        public Object categoryNameTranslates;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class LocalEvent extends Event {

        @JsonProperty("startDate")
        public void setDate(Date date) { super.setDate(date); }

        @JsonProperty("eventName")
        public void setSideNames(String eventName) throws ScrapperException {

            eventName = eventName.trim();
            String names[] = eventName.split(" - ");

            if(names.length != 2 ) {

                logger.error("Could not parse side names for eventName={} split_length={}", eventName, names.length);
                throw new ScrapperException("Could not parse side eventName for event=" + eventName);
            }

            super.setSideOne(names[0]);
            super.setSideTwo(names[1]);
        }


    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonDeserialize(using = LocalCategoryDeserializer.class)
    protected static class LocalCategory extends Category {
        public LocalCategory(String name, Long id) {
            super(name, id);
        }
    }

    protected static class LocalCategoryDeserializer extends JsonDeserializer<LocalCategory> {

        @Override
        public LocalCategory deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {

            SportCountryCategory sportCountryCategory = jp.readValueAs(SportCountryCategory.class);
            LocalCategory category = new LocalCategory(sportCountryCategory.name, sportCountryCategory.id);

            for (CategoryContract categoryContract : sportCountryCategory.categoryContracts) {
                Category subCategory = new Category(categoryContract.name, categoryContract.id);

                category.addSubCategory(subCategory);
            }

            return category;
        }
    }

    @JsonDeserialize(using = LocalEventListDeserializer.class)
    protected static class LocalEventList extends ArrayList<LocalEvent> { }

    protected static class LocalEventListDeserializer extends JsonDeserializer<List<LocalEvent>> {

        @Override
        public List<LocalEvent> deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {

            ArrayList<LocalEvent> resultEvents = new ArrayList<>();
            ObjectMapper mapper = new ObjectMapper();

            TreeNode root = jp.readValueAsTree();
            //TreeNode events = root.get("events");

            if(!root.isObject()) {
                logger.error("Event list node was expected to be object node, but it is not.");
                throw new IOException(new ScrapperException("Event list node was expected to be object node, but it is not."));
            }

            JsonNode innerNode = null;

            //Get first element from root element
            ObjectNode rootAsObject = (ObjectNode) root;
            for(rootAsObject.fields(); rootAsObject.fields().hasNext();) {
                Map.Entry<String, JsonNode> node = rootAsObject.fields().next();

                innerNode = node.getValue();
                break;
            }

            if(innerNode == null) {
                logger.error("Could not get inner node");
                throw new IOException(new ScrapperException("Could not get inner node"));
            }

            ObjectNode eventList = (ObjectNode) innerNode.get("events");
            eventList.forEach(event -> {

                LocalEvent nativeEvent = null;
                try {
                    nativeEvent = mapper.treeToValue(event, LocalEvent.class);
                    resultEvents.add(nativeEvent);
                } catch (JsonProcessingException e) {

                    logger.warn("Json processing exception while trying to create native event object from json.");
                    //throw new UncheckedIOException(e);
                }
            });

            return resultEvents;
        }
    }

    /*protected static class LocalEventOddsDeserializer extends JsonDeserializer<Map<OddType, Double>> {

        @Override
        public Map<OddType, Double> deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {

            Map<OddType, Double> result = new HashMap<>();

            Map<String, OddType> adjaraOddNameMapping = new HashMap<>();
            adjaraOddNameMapping.put("1", OddType._1);
            adjaraOddNameMapping.put("X", OddType._X);
            adjaraOddNameMapping.put("2", OddType._2);
            adjaraOddNameMapping.put("1X", OddType._1X);
            adjaraOddNameMapping.put("12", OddType._12);
            adjaraOddNameMapping.put("X2", OddType._X2);
            adjaraOddNameMapping.put("Under", OddType._UNDER_25);
            adjaraOddNameMapping.put("Over", OddType._OVER_25);
            adjaraOddNameMapping.put("Yes", OddType._YES);
            adjaraOddNameMapping.put("No", OddType._NO);

            JsonNode node = jp.readValueAsTree();
            Iterator<Map.Entry<String,JsonNode>> fields = node.fields();

            while (fields.hasNext()) {
                Map.Entry<String,JsonNode> entry = fields.next();

                //Skip this field
                if(entry.getKey().equals("0-47")) {
                    continue;
                }

                JsonNode oddGroup = entry.getValue();
                Iterator<JsonNode> elements = oddGroup.elements();
                while (elements.hasNext()){
                    JsonNode odd = elements.next();

                    OddType oddType = adjaraOddNameMapping.get(odd.get("n").asText());

                    result.put(oddType, odd.get("v").asDouble());
                }
            }

            return result;
        }

    }*/

    /*protected static class LocalEventDateDeserializer extends AbstractDateDeserializer {

        //2017-11-24T19:30:00.000+0000
        @Override
        protected String getPattern() {
            return "";
        }

    }*/

    public BetLiveScraper() {

    }

    public List<? extends Category> getFreshData() throws ScrapperException {
        logger.info("Start scraping");

        try {
            return parseCategories();
        } catch (IOException e) {
            logger.error("Error while trying to parse categories {}", e);
            throw new ScrapperException(e);
        }
    }

    protected List<? extends Category> parseCategories() throws ScrapperException, IOException {
        String sportBookTree = Http.get("https://sport.betlive.com/en-US/api/category/getSportCountryCategories?languageId=1&sportId=" + SOCCER_ID + "&time=100");

        ObjectMapper mapper = new ObjectMapper();
        List<LocalCategory> categories = mapper.readValue(sportBookTree, new TypeReference<ArrayList<LocalCategory>>() {});

        categories.forEach(category -> {
            category.getSubCategories().forEach(subCategory -> {

                try {
                    List<LocalEvent> events = parseEvents(subCategory.getId());

                    events.forEach(event -> {
                        subCategory.addEvent(event);
                    });

                } catch (ScrapperException e) {
                    throw new UncheckedScrapperException(e);
                }
            });
        });

        return categories;
    }

    protected List<LocalEvent> parseEvents(Long subCategoryId) throws ScrapperException {

        ObjectMapper mapper = new ObjectMapper();

        List<LocalEvent> events = null;
        for (int page = 0; page < MAX_PAGE_COUNT; page++) {
            events = null;
            try {
                String executeString = Http.get("https://sport.betlive.com/en-US/Prematch/LeaguesJson/?idList="
                        + subCategoryId +
                        "&gametype=null&state=null&sportid=null&time=100&page=0");

                events = mapper.readValue(executeString, LocalEventList.class);
            } catch (IOException e) {

                if(page == 0) {
                    logger.error("Cannot get event list, http request failed or could not deserialize data. {}", e);
                    throw new ScrapperException(e);
                }

                //Pages finished.
                break;
            }
        }

        return events;
    }
}
