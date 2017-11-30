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
import ge.shitbot.scraper.BookieScraper;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import ge.shitbot.scraper.exceptions.ScrapperException;
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

    public List<? extends Category> getFreshData() throws ScrapperException {
        logger.info("Start scraping");

        try {
            List<? extends Category> result = parseCategories();
            logger.info("End scraping");

            return result;

        } catch (Exception e) {

            logger.error("Scraping failed {}", e);
            e.printStackTrace();
            throw new ScrapperException(e);
        }
    }

    protected String getJsonDataNodeFromUrl(String url) throws IOException {
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

        JsonFactory factory = new JsonFactory();
        JsonParser jp = factory.createParser(inputStream);
        jp.setCodec(new ObjectMapper());
        TreeNode root = jp.readValueAsTree();
        TreeNode dataNode = root.get("data");
        return dataNode.toString();
    }

    protected List<? extends Category> parseCategories() throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        String data = getJsonDataNodeFromUrl(getSearchUrl());

        ArrayList<LocalCategory> allCategories = mapper.readValue(data, new TypeReference<ArrayList<LocalCategory>>() { });

        //Filter only top categories - Sports
        List<LocalCategory> sports = allCategories.stream()
                .filter(elem -> elem.getLocalParentId().equals(0)  && elem.getLocalLevel().equals(1)
                        // Take football
                        && elem.getId().equals(1))
                .collect(Collectors.toList());

        long categoriesParsed = sports.stream().filter(el -> el.getLocalLevel().equals(2)).count();
        long subCategoryCount = sports.stream().filter(el -> el.getLocalLevel().equals(3)).count();
        long parsedEventCount = sports.stream().filter(el -> el.getLocalLevel().equals(4)).count();

        //Add categories to each sport
        sports.stream().forEach(sport -> {

            allCategories.stream().forEach(category -> {

                //If current items parent id matches current sporsId. Add category to sport
               if(category.getLocalParentId().equals(sport.getId()) && category.getLocalLevel().equals(2)) {

                   sport.addSubCategory(category);

                   //Add subcategories to category
                   allCategories.stream().forEach(subCategory -> {

                       // If current ite
                       if(subCategory.getLocalParentId().equals(category.getId()) && subCategory.getLocalLevel().equals(3)) {
                           category.addSubCategory(subCategory);

                           try {

                               if(category.getName().equals("International")) {
                                   //Out rights are europe specific thing and we skip these
                                   if(subCategory.getName().equals("Outrights")) { return; }
                               }

                               ArrayList<LocalEvent> events = parseEvents(subCategory);

                               events.stream().forEach(event -> {
                                   subCategory.addEvent(event);
                               });

                           } catch (ScrapperException e) {

                               logger.error("Events parsing failed for subCategory={} and id={}", subCategory.getName(), subCategory.getId());

                               e.printStackTrace();
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

    protected ArrayList<LocalEvent> parseEvents(Category surroundingCategory) throws ScrapperException {
        try {

            ObjectMapper mapper = new ObjectMapper();
            String data = getJsonDataNodeFromUrl(getEventsUrl(surroundingCategory.getId()));

            return mapper.readValue(data, new TypeReference<ArrayList<LocalEvent>>() { });

        } catch (Exception e) {

            logger.error("Could not get events for subcategory {} id={} and category {} id={}",
                    surroundingCategory.getName(), surroundingCategory.getId(),
                    surroundingCategory.getParent().getName(), surroundingCategory.getParent().getId());
            throw new ScrapperException(e);
        }
    }
}