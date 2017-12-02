package ge.shitbot.scraper.bookies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ge.shitbot.core.datatypes.OddType;
import ge.shitbot.core.datatypes.deserialize.AbstractDateDeserializer;
import ge.shitbot.scraper.BookieScraper;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import ge.shitbot.scraper.exceptions.ScraperException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created by giga on 11/22/17.
 */
public class AdjaraBetScraper implements BookieScraper {

    private static Logger logger = LoggerFactory.getLogger(CrystalBetScraper.class);

    protected Long SOCCER_ID = 27L;

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

        @JsonProperty("h")
        @Override
        public void setSideOne(String sideOne) {
            super.setSideOne(sideOne);
        }

        @JsonProperty("a")
        @Override
        public void setSideTwo(String sideTwo) {
            super.setSideTwo(sideTwo);
        }

        @JsonDeserialize(using = LocalEventDateDeserializer.class)
        @JsonProperty("sd")
        @Override
        public void setDate(Date date) {
            super.setDate(date);
        }

        @JsonProperty("t")
        @JsonDeserialize(using = LocalEventOddsDeserializer.class)
        @Override
        public void setOdds(Map<OddType, Double> odds) {
            super.setOdds(odds);
        }
    }

    protected static class LocalEventOddsDeserializer extends JsonDeserializer<Map<OddType, Double>> {

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

    }

    protected static class LocalEventDateDeserializer extends AbstractDateDeserializer {

        //2017-11-24T19:30:00.000+0000
        @Override
        protected String getPattern() {
            return "";
        }

    }

    public AdjaraBetScraper() {

    }

    public List<? extends Category> getFreshData() throws ScraperException {
        logger.info("Start scraping");

        try {
            return parseCategories();
        } catch (IOException e) {
            logger.error("Error while trying to parse categories {}", e);
            throw new ScraperException(e);
        }
    }

    protected List<? extends Category> parseCategories() throws ScraperException, IOException {
        String sportBookTree = executeGet("https://bookmakersapi.adjarabet.com/sportsbook/rest/public/sportbookTree?ln=en");

        ObjectMapper mapper = new ObjectMapper();
        AdjarabetTree adjarabetTree = mapper.readValue(sportBookTree, AdjarabetTree.class);

        ArrayList<Category> categories = new ArrayList<>();

        long categoriesParsed = 0;
        long subCategoryCount = 0;

        // Sports
        for (S sport : adjarabetTree.s) {

            // Parse only Soccer
            if (!sport.id.equals(SOCCER_ID)) {
                break;
            }

            // Categories
            for (C category : sport.c) {

                // Create native Category from Adjara's category
                Category nativeCategory = new Category(category.n, category.id);
                categories.add(nativeCategory);

                categoriesParsed++;

                // Subcategory - League
                for (L subCategory : category.l) {

                    // Create native Category from Adjara's category
                    Category nativeSubCategory = new Category(subCategory.n, subCategory.id);

                    nativeCategory.addSubCategory(nativeSubCategory);

                    subCategoryCount++;

                    AdjaraNames dic = new AdjaraNames();
                    dic.sport = sport.n;
                    dic.sportId = sport.id;
                    dic.category = category.n;
                    dic.categoryId = category.id;
                    dic.subCategory = subCategory.n;
                    dic.subCategoryId = subCategory.id;
                }
            }
        }

        Map<Long, List<LocalEvent>> events = getAllEventsForSport();

        logger.info("Categories parsed: {}", categoriesParsed);
        logger.info("Subcategories parsed: {}", subCategoryCount);
        logger.info("Events parsed: {}", events.size());

        return mapEventsToCategories(categories, events);
    }

    protected Map<Long, List<LocalEvent>> getAllEventsForSport() throws ScraperException {

        ObjectMapper mapper = new ObjectMapper();

        Map<Long, List<LocalEvent>> events = null;
        try {
            String executeString = executeGet("https://bookmakersapi.adjarabet.com/sportsbook/rest/public/sportMatches?ln=en&id=" + SOCCER_ID);

            //CONTINUE
            events = mapper.readValue(executeString, new TypeReference<Map<Long, List<LocalEvent>>>() {});
        } catch (IOException e) {
            logger.error("Cannot get event list, http request failed or could not deserialize data. {}", e);
            throw new ScraperException(e);
        }

        return events;
    }

    protected List<? extends Category> mapEventsToCategories(List<? extends Category> categories, Map<Long, List<LocalEvent>> events) {

        for (Category category : categories) {
            for (Category subCategory : category.getSubCategories()) {

                try {

                    //Filter events for this subCategory
                    List<LocalEvent> subCategoryEvents = events.get(subCategory.getId());

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

    protected String executeGet(String url) throws IOException {
        HttpGet get = null;
        try {
            get = new HttpGet(new URI(url));
        } catch (URISyntaxException e) {

            logger.error("Bad URI syntax: " + e);
            e.printStackTrace();
        }

        get.setHeader("Request-Language", "en");

        HttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(get);
        InputStream inputStream = response.getEntity().getContent();

        return convertStreamToString(inputStream);
    }

    static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
