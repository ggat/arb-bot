package ge.arb-bot.scraper.bookies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ge.arb-bot.core.datatypes.OddType;
import ge.arb-bot.core.datatypes.deserialize.AbstractDateDeserializer;
import ge.arb-bot.core.datatypes.util.http.Http;
import ge.arb-bot.scraper.BookieScraper;
import ge.arb-bot.scraper.datatypes.Category;
import ge.arb-bot.scraper.datatypes.Event;
import ge.arb-bot.scraper.exceptions.ScraperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by giga on 11/12/17.
 */
public abstract class AbstractEuropeCrocoScraper implements BookieScraper {

    private static Logger logger = LoggerFactory.getLogger(AbstractEuropeCrocoScraper.class);

    public static void setLogger(Logger logger) {
        AbstractEuropeCrocoScraper.logger = logger;
    }

    public AbstractEuropeCrocoScraper() {
        logger = LoggerFactory.getLogger(this.getClass().getName());
    }

    public abstract String getSearchUrl();

    public abstract String getEventsUrl(Long subCategoryId);

    public abstract String getOutRightsString();

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class LocalCategory extends Category {

        Long localParentId;
        Long localLevel;

        public Long getLocalLevel() {
            return localLevel;
        }

        @JsonProperty("level")
        public void setLocalLevel(Long localLevel) {
            this.localLevel = localLevel;
        }

        public Long getLocalParentId() {
            return localParentId;
        }

        @JsonProperty("parentCategory")
        public void setLocalParentId(Long localParentId) {
            this.localParentId = localParentId;
        }

        @Override
        @JsonProperty("categoryName")
        public void setName(String name) {
            super.setName(name);
        }

        @Override
        @JsonProperty("categoryId")
        public void setId(Long id) {
            super.setId(id);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    protected static class LocalEvent extends Event {

        private static final long serialVersionUID = -8427487129119333649L;

        @JsonProperty("eventStart")
        @JsonDeserialize(using = LocalEventDateDeserializer.class)
        @Override
        public void setDate(Date date) {
            super.setDate(date);
        }

        @JsonProperty("eventName")
        public void setSideNames(String eventName) throws ScraperException {

            eventName = eventName.trim();
            String names[] = eventName.split(" - ");

            if(names.length != 2 ) {

                logger.warn("Could not parse side names for eventName={} split_length={}", eventName, names.length);
                throw new ScraperException("Could not parse side eventName for event=" + eventName);
            }

            super.setSideOne(names[0]);
            super.setSideTwo(names[1]);
        }

        @JsonProperty("eventGames")
        @JsonDeserialize(using = LocalEventOddsDeserializer.class)
        @Override
        public void setOdds(Map<OddType, Double> odds) {
            super.setOdds(odds);
        }

        @Override
        public void setSideTwo(String sideTwo) {
            super.setSideTwo(sideTwo);
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
            //return new Date(unixSeconds*1000L);
            //TODO: Removed multply on 1000 cause it seems it is in milliseconds already. NOT TESTED IF IT IS CORRECT
            return new Date(unixSeconds);
        }
    }

    protected abstract String[] getList();

    protected static class LocalEventOddsDeserializer extends JsonDeserializer<Map<OddType, Double>> {

        // Game types we currently take cate of
        protected String[] engGameTypes = {"1X2 *", "Double Chance *", "Under/Over 2.5 goals *", "Both teams to score *"};

        //TODO: Enable after gameTypes for other langs are added, also we need to get langUsed from somwhere.
        //if(Language.ENGLISH == langUsed) {
        List<String> list = new ArrayList<>(Arrays.asList(getEngGameTypes()));
        //}

        public String[] getEngGameTypes() {
            return engGameTypes;
        }

        protected Map<String, OddType> getOddNameMapping() {

            /*Map<String, OddType> oddNameMapping = new HashMap<>();
            oddNameMapping.put("1", OddType._1);
            oddNameMapping.put("X", OddType._X);
            oddNameMapping.put("2", OddType._2);
            oddNameMapping.put("1/X", OddType._1X);
            oddNameMapping.put("1/2", OddType._12);
            oddNameMapping.put("X/2", OddType._X2);
            oddNameMapping.put("Under", OddType._UNDER_25);
            oddNameMapping.put("Over", OddType._OVER_25);
            oddNameMapping.put("Yes", OddType._YES);
            oddNameMapping.put("No", OddType._NO);*/

            return new HashMap<>();
        }

        @Override
        public Map<OddType, Double> deserialize(JsonParser jp, DeserializationContext ctx) throws IOException {

            Map<OddType, Double> result = new HashMap<>();

            Map<String, OddType> oddNameMapping = getOddNameMapping();

            JsonNode node = jp.readValueAsTree();
            Iterator<JsonNode> oddGroups = node.elements();

            while (oddGroups.hasNext()) {
                JsonNode oddGroup = oddGroups.next();
                String gameT = oddGroup.get("gameName").asText();

                if (list.stream().anyMatch(type -> type.equals(gameT))) {
                    JsonNode odds = oddGroup.get("outcomes");

                    for (JsonNode odd : odds) {
                        OddType oddType = null;
                        String parsedOddTypeName = odd.get("outcomeName").asText();
                        try {
                            oddType = oddNameMapping.get(parsedOddTypeName);
                        } catch (NullPointerException e) {
                            logger.warn("Unknown odd type string parsed \"{}\"", parsedOddTypeName);
                        }
                        result.put(oddType, odd.get("outcomeOdds").asDouble());
                    }
                }
            }

            return result;
        }

    }

    public List<? extends Category> getFreshData() throws ScraperException {
        logger.info("Start scraping");

        try {
            List<? extends Category> result = parseCategories();
            logger.info("End scraping");

            return result;

        } catch (Exception e) {

            logger.error("Scraping failed {}", e);
            e.printStackTrace();
            throw new ScraperException(e);
        }
    }

    protected String getJsonDataNodeFromUrl(String url) throws IOException {
        /*HttpGet get = null;
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

        JsonFactory factory = new JsonFactory();
        JsonParser jp = factory.createParser(inputStream);
        jp.setCodec(new ObjectMapper());*/

        HashMap<String, String> headers = new HashMap<>();
        headers.put("Request-Language", "en");

        String response = Http.get(url, headers);

        JsonFactory factory = new JsonFactory();
        JsonParser jp = factory.createParser(response);
        jp.setCodec(new ObjectMapper());

        TreeNode root = jp.readValueAsTree();
        TreeNode dataNode = root.get("data");
        return dataNode.toString();
    }

    protected List<? extends Category> parseCategories() throws IOException, ScraperException {

        ObjectMapper mapper = new ObjectMapper();
        String data = getJsonDataNodeFromUrl(getSearchUrl());

        ArrayList<LocalCategory> allCategories = mapper.readValue(data, new TypeReference<ArrayList<LocalCategory>>() { });

        //Filter only top categories - Sports
        List<LocalCategory> sports = allCategories.stream()
                .filter((LocalCategory elem) -> {
                    return elem.getLocalParentId().equals(0L) && elem.getLocalLevel().equals(1L)
                            // Take football
                            && elem.getId().equals(1L);
                })
                .collect(Collectors.toList());

        if(sports.size() < 1) {
            throw new ScraperException("Sport list is less than 1 probably soccer id changed.");
        }

        long categoriesParsed = sports.stream().filter(el -> el.getLocalLevel().equals(2L)).count();
        long subCategoryCount = sports.stream().filter(el -> el.getLocalLevel().equals(3L)).count();
        long parsedEventCount = sports.stream().filter(el -> el.getLocalLevel().equals(4L)).count();

        //FIXME: See fixme that starts with !!! below
        List<Category> categories = new ArrayList<>();

        //Add categories to each sport
        sports.stream().forEach(sport -> {

            allCategories.stream().forEach(category -> {

                //If current items parent id matches current sporsId. Add category to sport
               if(category.getLocalParentId().equals(sport.getId()) && category.getLocalLevel().equals(2L)) {

                   //TODO: Look at me
                   //FIXME: !!! We have problem when sport is parent category of category like league.
                   // Cause some scrapers parse sports and some not and then we have a code that accounts on parent
                   // categories to map Categories to CategoryInfos for example. CategoryCategoryInfoMapper.map()
                   // and that fails because of this.
                   //sport.addSubCategory(category);
                   categories.add(category);

                   //Add subcategories to category
                   allCategories.stream().forEach(subCategory -> {

                       // If current ite
                       if(subCategory.getLocalParentId().equals(category.getId()) && subCategory.getLocalLevel().equals(3L)) {
                           category.addSubCategory(subCategory);

                           try {

                               //Out rights are europe specific thing and we skip these
                               if(subCategory.getName().contains(getOutRightsString())) {
                                   return;
                               }

                               ArrayList<LocalEvent> events = parseEvents(subCategory);

                               events.stream().forEach(event -> {
                                   subCategory.addEvent(event);
                               });

                           } catch (ScraperException e) {

                               logger.warn("Events parsing failed for subCategory={} and id={}", subCategory.getName(), subCategory.getId());
                           }
                       }
                   });
               }
            });
        });

        logger.info("Categories parsed: {}", categoriesParsed);
        logger.info("Subcategories parsed: {}", subCategoryCount);
        logger.info("Events parsed: {}", parsedEventCount);

        //Get 0 just because we only need Soccer yet. And above we take only it into sports.
        //return sports.get(0).getSubCategories();

        //FIXME: See fixme that starts with !!! above
        return categories;
    }

    static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    protected ArrayList<LocalEvent> parseEvents(Category surroundingCategory) throws ScraperException {
        try {
            String data = getJsonDataNodeFromUrl(getEventsUrl(surroundingCategory.getId()));

            return mapLocalEvents(data);

        } catch (Exception e) {

            logger.debug("Could not get events for subcategory {} id={} and category {} id={}",
                    surroundingCategory.getName(), surroundingCategory.getId(),
                    surroundingCategory.getParent().getName(), surroundingCategory.getParent().getId());
            throw new ScraperException(e);
        }
    }

    protected abstract ArrayList<LocalEvent> mapLocalEvents(String data) throws Exception;
}
