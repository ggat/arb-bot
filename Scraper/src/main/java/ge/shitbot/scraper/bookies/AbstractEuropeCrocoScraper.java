package ge.shitbot.scraper.bookies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ge.shitbot.core.datatypes.deserialize.AbstractDateDeserializer;
import ge.shitbot.core.datatypes.util.http.Http;
import ge.shitbot.scraper.BookieScraper;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import ge.shitbot.scraper.exceptions.ScraperException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

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
            return new Date(unixSeconds*1000L);
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

        //Add categories to each sport
        sports.stream().forEach(sport -> {

            allCategories.stream().forEach(category -> {

                //If current items parent id matches current sporsId. Add category to sport
               if(category.getLocalParentId().equals(sport.getId()) && category.getLocalLevel().equals(2L)) {

                   sport.addSubCategory(category);

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
        return sports.get(0).getSubCategories();
    }

    static String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    protected ArrayList<LocalEvent> parseEvents(Category surroundingCategory) throws ScraperException {
        try {

            ObjectMapper mapper = new ObjectMapper();
            String data = getJsonDataNodeFromUrl(getEventsUrl(surroundingCategory.getId()));

            return mapper.readValue(data, new TypeReference<ArrayList<LocalEvent>>() { });

        } catch (Exception e) {

            logger.debug("Could not get events for subcategory {} id={} and category {} id={}",
                    surroundingCategory.getName(), surroundingCategory.getId(),
                    surroundingCategory.getParent().getName(), surroundingCategory.getParent().getId());
            throw new ScraperException(e);
        }
    }
}
