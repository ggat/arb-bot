package ge.shitbot.scraper.bookies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
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
import ge.shitbot.scraper.BookieScraper;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import ge.shitbot.scraper.exceptions.ScrapperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by giga on 11/22/17.
 */
public class LiderBetScraper implements BookieScraper {

    private static Logger logger = LoggerFactory.getLogger(LiderBetScraper.class);

    protected Long SOCCER_ID = 16L;

    protected static class AdjaraNames {
        public String sport;
        public Long sportId;
        public String category;
        public Long categoryId;
        public String subCategory;
        public Long subCategoryId;
    }

    protected static class L {
        public Long id;
        public String n;
        public Long nb;
    }

    protected static class C {
        public Long id;
        public String n;
        public String c;
        public Long nb;
        public Long p;
        public List<L> l;
    }

    protected static class S {
        public Long priority;
        public Long id;
        public String n;
        public Long nb;
        public String s;
        public List<C> c;
    }

    protected static class AdjarabetTree {
        public List<Integer> f;
        public List<S> s;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class LocalEvent extends Event {

        Long localSubCategoryId;

        public Long getLocalSubCategoryId() {
            return localSubCategoryId;
        }

        @JsonProperty("s2")
        public void setLocalSubCategoryId(Long localSubCategoryId) {
            this.localSubCategoryId = localSubCategoryId;
        }

        @JsonProperty("team1")
        @Override
        public void setSideOne(String sideOne) {
            super.setSideOne(sideOne);
        }

        @JsonProperty("team2")
        @Override
        public void setSideTwo(String sideTwo) { super.setSideTwo(sideTwo); }

        @JsonProperty("i")
        @JsonDeserialize(using = LocalEventOddsDeserializer.class)
        @Override
        public void setOdds(Map<OddType, Double> odds) {
            super.setOdds(odds);
        }

        @JsonProperty("t")
        @JsonDeserialize(using = LocalEventDateDeserializer.class)
        @Override
        public void setDate(Date date) {
            super.setDate(date);
        }
    }


    @JsonDeserialize(using = LocalCategoryDeserializer.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class LocalCategory extends Category {

        protected Long localParentId;

        public Long getLocalParentId() {
            return localParentId;
        }

        public void setLocalParentId(Long localParentId) {
            this.localParentId = localParentId;
        }

        public LocalCategory(String name, Long id, Long localParentId) {
            super(name, id);
            this.localParentId = localParentId;
        }
    }

    @JsonDeserialize(using = LocalSubCategoryDeserializer.class)
    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class LocalSubCategory extends LocalCategory {
        public LocalSubCategory(String name, Long id, Long localParentId) {
            super(name, id, localParentId);
        }
    }

    protected static class LocalEventOddsDeserializer extends JsonDeserializer<Map<OddType, Double>> {

        @Override
        public Map<OddType, Double> deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {

            Map<OddType, Double> result = new HashMap<>();

            Map<String, OddType> oddNameMapping = new HashMap<>();
            oddNameMapping.put("1", OddType._1);
            oddNameMapping.put("X", OddType._X);
            oddNameMapping.put("2", OddType._2);
            oddNameMapping.put("1X", OddType._1X);
            oddNameMapping.put("12", OddType._12);
            oddNameMapping.put("X2", OddType._X2);
            oddNameMapping.put("Under", OddType._UNDER_25);
            oddNameMapping.put("Over", OddType._OVER_25);
            oddNameMapping.put("Yes", OddType._YES);
            oddNameMapping.put("No", OddType._NO);

            JsonNode node = jp.readValueAsTree();
            Iterator<JsonNode> oddGroups = node.elements();

            while (oddGroups.hasNext()) {
                JsonNode oddGroup = oddGroups.next();
                JsonNode odds = oddGroup.get("i");

                for(JsonNode odd : odds) {
                    OddType oddType = oddNameMapping.get(odd.get("n").asText());
                    result.put(oddType, odd.get("v").asDouble());
                }
            }

            return result;
        }

    }

    protected static class LocalEventDateDeserializer extends AbstractDateDeserializer {

        @Override
        protected String getPattern(){
            return "";
        }

        @Override
        protected Date convert(String str, DeserializationContext ctx) {
            long unixSeconds = Long.parseLong(str);
            return new Date(unixSeconds*1000L);
        }
    }

    protected static abstract class LocalAbstractCategoryDeserializer extends JsonDeserializer<LocalCategory> {

        protected abstract Integer getParentIdIndex();

        @Override
        public LocalCategory deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {

            JsonNode node = jp.readValueAsTree();

            Long id = null;
            Long parentId = null;
            String categoryName = null;
            try {
                id = Long.parseLong(jp.getCurrentName());
                parentId = node.get(getParentIdIndex()).asLong();
                categoryName = node.get(0).asText();
            } catch (NumberFormatException e) {

                //TODO: log it

                throw new IOException(e);
            } catch (IOException e) {

                //TODO: log it

                e.printStackTrace();
            }

            return new LocalSubCategory(categoryName, id, parentId);
        }
    }

    protected static class LocalCategoryDeserializer extends LocalAbstractCategoryDeserializer {

        @Override
        protected Integer getParentIdIndex() {
            return 1;
        }
    }

    protected static class LocalSubCategoryDeserializer extends LocalAbstractCategoryDeserializer {

        @Override
        protected Integer getParentIdIndex() {
            return 2;
        }
    }

    public LiderBetScraper() {

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
        String data = Http.get("https://sportcache.lider-bet.com/search/data_en.json");

        long categoriesParsed = 0;
        long subCategoryCount = 0;

        ObjectMapper mapper = new ObjectMapper();
        JsonNode tree = mapper.readTree(data).get("dict");
        JsonNode categoryNodes = tree.get("c");
        JsonNode subCategoryNodes = tree.get("t");

        Map<Long, LocalCategory> categories = mapper.readValue(tree.get("c").toString(), new TypeReference <Map<Long, LocalCategory>> () {});
        Map<Long, LocalSubCategory> subCategories = mapper.readValue(tree.get("t").toString(), new TypeReference <Map<Long, LocalSubCategory>> () {});

        categories = categories.entrySet().stream().filter(entry -> {
            return entry.getValue().getLocalParentId().equals(SOCCER_ID);
        }).collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue()));

        for (Map.Entry<Long, LocalSubCategory> subCategory : subCategories.entrySet()) {

            Long subCategoryParentId = subCategory.getValue().getLocalParentId();

            categories.values().stream().forEach(category -> {
                if(category.getId().equals(subCategoryParentId)) {

                    category.addSubCategory(subCategory.getValue());
                }
            });
        }

        if( !(categoryNodes instanceof ObjectNode) && !(subCategoryNodes instanceof ObjectNode)) {
            logger.error("Received unexpected data.");
            throw new ScrapperException("Received unexpected data. categoryNodes and subCategoryNodes at leas one of " +
                    "them is not ObjectNode");
        }

        Map<Long, List<LocalEvent>> events = getAllEventsForSport();

        logger.info("Categories parsed: {}", categoriesParsed);
        logger.info("Subcategories parsed: {}", subCategoryCount);
        logger.info("Events parsed: {}", events.size());

        return mapEventsToCategories(new ArrayList<>(categories.values()), events);
    }

    public Map<Long, List<LocalEvent>> getAllEventsForSport() throws ScrapperException {

        ObjectMapper mapper = new ObjectMapper();

        Map<Long, List<LocalEvent>> events = null;
        try {
            String responseString = Http.get("https://www.lider-bet.com/api/?app=website/sport/get_all&_withlive&lng=en&get=sport&sport=" + SOCCER_ID);
            JsonNode tree = mapper.readTree(responseString);

            if( !(tree instanceof ObjectNode) ) {
                logger.error("Unknown data structure received. Root object for events is expected to be ObjectNode but it is not.");
                throw new ScrapperException("Unknown data structure received. Root object for events is expected to be ObjectNode but it is not.");
            }

            ((ObjectNode) tree).remove("-1");

            String fixedTree = tree.toString();

            events = mapper.readValue(fixedTree, new TypeReference<Map<Long, List<LocalEvent>>>() {});
        } catch (IOException e) {
            logger.error("Cannot get event list, http request failed or could not deserialize data. {}", e);
            throw new ScrapperException(e);
        }

        return events;
    }

    protected List<? extends Category> mapEventsToCategories(List<? extends LocalCategory> categories, Map<Long, List<LocalEvent>> events) {

        for (LocalCategory category : categories) {
            for (Category subCategory : category.getSubCategories()) {

                try {

                    Long key = subCategory.getId();

                    //Filter events for this subCategory
                    List<LocalEvent> subCategoryEvents = events.get(key);

                    //Add these events to this subCategory
                    for (LocalEvent event : subCategoryEvents) {
                        subCategory.addEvent(event);
                    }

                } catch (NullPointerException ex) {
                    logger.warn("No events found for subCategory.n={} subCategory.id={}", subCategory.getName(), subCategory.getId());
                }
            }
        }

        return categories;
    }
}
