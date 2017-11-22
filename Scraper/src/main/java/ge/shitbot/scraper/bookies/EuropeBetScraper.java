package ge.shitbot.scraper.bookies;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.core.datatypes.deserialize.AbstractDateDeserializer;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import ge.shitbot.scraper.exceptions.ScrapperException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.HttpClients;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Tidy;
import sun.reflect.generics.tree.Tree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by giga on 11/12/17.
 */
public class EuropeBetScraper {

    private static Logger logger = LoggerFactory.getLogger(EuropeBetScraper.class);

    //String searchUrl = "https://sport2.europebet.com/rest/market/categories?_=1511257613714";
    String searchUrl = "https://sport2.europebet.com/rest/market/categories?_=" + (new Random().nextInt(999999999) + 1);

    public String getEventsUrl(Integer subCategoryId) {
        return "https://sport2.europebet.com/rest/market/categories/"+ subCategoryId +"/events?_=1511269825840";
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class LocalCategory extends Category {

        Integer localParentId;
        Integer localLevel;

        public Integer getLocalLevel() {
            return localLevel;
        }

        @JsonProperty("level")
        public void setLocalLevel(Integer localLevel) {
            this.localLevel = localLevel;
        }

        public Integer getLocalParentId() {
            return localParentId;
        }

        @JsonProperty("parentCategory")
        public void setLocalParentId(Integer localParentId) {
            this.localParentId = localParentId;
        }

        @Override
        @JsonProperty("categoryName")
        public void setName(String name) {
            super.setName(name);
        }

        @Override
        @JsonProperty("categoryId")
        public void setId(Integer id) {
            super.setId(id);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class LocalEvent extends Event {
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
            super.setSideOne(names[1]);
        }

        @Override
        public void setSideTwo(String sideTwo) {
            super.setSideTwo(sideTwo);
        }
    }

    private static class LocalEventDateDeserializer extends AbstractDateDeserializer {

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

    public EuropeBetScraper() {

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

    protected List<? extends Category> parseCategories() throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        HttpGet get = null;
        try {
            get = new HttpGet(new URI(searchUrl));
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
        String data = dataNode.toString();


        String input = convertStreamToString(inputStream);

        System.out.println(input);

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

            /*if(!sport.getId().equals(1)){
                return;
            }*/

            allCategories.stream().forEach(category -> {

               if(category.getLocalParentId().equals(sport.getId()) && category.getLocalLevel().equals(2)) {

                   sport.addSubCategory(category);

                   allCategories.stream().forEach(subCategory -> {
                       if(subCategory.getLocalParentId().equals(category.getId()) && subCategory.getLocalLevel().equals(3)) {
                           category.addSubCategory(subCategory);

                           try {

                               //Out rights are europe specific thing and we skip these
                               if(subCategory.getName().equals("Outrights")) { return; }

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

        return allCategories;
    }

    static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    protected ArrayList<LocalEvent> parseEvents(Category surroundingCategory) throws ScrapperException {
        try {

            ObjectMapper mapper = new ObjectMapper();

            URI eventUri = new URI(getEventsUrl(surroundingCategory.getId()));
            HttpGet get = new HttpGet(eventUri);

            get.setHeader("Request-Language", "en");

            HttpClient client = HttpClients.createDefault();
            HttpResponse response = client.execute(get);
            InputStream inputStream = response.getEntity().getContent();

            JsonFactory factory = new JsonFactory();
            JsonParser jp = factory.createParser(inputStream);
            jp.setCodec(new ObjectMapper());
            TreeNode root = jp.readValueAsTree();
            TreeNode dataNode = root.get("data");
            String data = dataNode.toString();

            return mapper.readValue(data, new TypeReference<ArrayList<LocalEvent>>() { });

        } catch (URISyntaxException e) {

            logger.error("Could not create events URI. Caused by {}", e);
            throw new ScrapperException(e);
        } catch (Exception e) {

            logger.error("Could not get events for subcategory {} id={} and category {} id={}",
                    surroundingCategory.getName(), surroundingCategory.getId(),
                    surroundingCategory.getParent().getName(), surroundingCategory.getParent().getId());
            throw new ScrapperException(e);
        }
    }

    private Date getDate(String dateTime) throws ParseException {

        String currentYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        dateTime = currentYear + "/" + dateTime.trim();

        SimpleDateFormat format1 = new SimpleDateFormat("yyyy/dd/MM hh:mm");

        return format1.parse(dateTime);
    }

    protected String tidy(String content) {
        Tidy tidy = new Tidy();
        tidy.setCharEncoding(Configuration.UTF8);
        //tidy.setXHTML(true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        tidy.parse(new ByteArrayInputStream(content.getBytes()), outputStream);

        return (new String(outputStream.toByteArray())).trim();
    }

    protected String removeLines(String initial) {
        String[] lines = initial.split("\n");

        List<String> lineList = new ArrayList<>(Arrays.asList(lines));

        lineList.remove(0);
        lineList.remove(lineList.size()-1);
        lineList.remove(lineList.size()-1);
        lineList.remove(lineList.size()-1);

        String[] newLines = lineList.toArray(new String[lineList.size()]);

        return String.join("\n", newLines);
    }

    public static void main(String[] args) {
        new EuropeBetScraper();
    }
}
