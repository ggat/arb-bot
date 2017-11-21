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
    String sessionId;
    String postData = "ctl00%24ctl00%24MasteScriptManager=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdatePanelsHolder%7Cctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&ctl00%24ctl00%24HiddenEventCode=&ctl00%24ctl00%24HiddenEventArgument=&ctl00%24ctl00%24HiddenFieldViewMode=1500&ctl00%24ctl00%24MainLoginView%24MainLogin%24Password=&ctl00%24ctl00%24MainLoginView%24MainLogin%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24SpecialGamesSports%24FieldSpecialGameIndex=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24tbSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24HiddenFieldTeamSearch=&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolderSportHeader%24hfTimeInterval=-169&ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24RepeaterChampionats%24ctl00%24HiddenFieldChampionatId=REPLACE_MEEEE&ctl00%24ctl00%24ContentPlaceHolder1%24CheckBoxAmountIsPrime=on&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24UserName=&ctl00%24ctl00%24ContentPlaceHolder1%24LoginInTicketSports%24Password=&ctl00%24ctl00%24ContentPlaceHolder1%24AfterTicket%24MiniGameUserControl%24HiddenFieldMiniGameType=Bura&ctl00%24ctl00%24LoginPopupLogin%24UserName=&ctl00%24ctl00%24LoginPopupLogin%24Password=&ctl00%24ctl00%24tbEmailForUserNameReset=&ctl00%24ctl00%24tbMobileNumberForUserReset=&ctl00%24ctl00%24tbPersonalNumberForUserReset=&ctl00%24ctl00%24tbEmailForPasswordReset=&ctl00%24ctl00%24tbUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSUserNameForPasswordReset=&ctl00%24ctl00%24tbSMSMobileNumberForPasswordReset=&ctl00%24ctl00%24tbSMSPersonalNumberForPasswordReset=&__EVENTTARGET=ctl00%24ctl00%24ContentPlaceHolder1%24ContentPlaceHolder2%24UpdateChampionats&__EVENTARGUMENT=REPLACE_MEEEE&__VIEWSTATE=4Ccer4UsqpQeWnpjWv%2BA3Kda%2F%2FZFMr8lgg9K3KZJFHE%2BzOSAFL2eKqNPJT9v9V0ui4c%2FOGcbrAwcyPjo7NWIVzO2C%2B%2BEmPfFDuQurqtAt344Hz5ojlwVYWF2JIYUQmTR4TihVIN3Ui4%2FEWmCOZ%2BrkRFNr2rM5uAPpQUGt6qb8ULXc1gKdjg%2B2NW1j0tJm99bLPOt%2F3aIgk8suP8UtA5tOP8iL1lp1BYx6zjPBenkUeVOrFqOZcDSlpGkuTJ3BEgAxzsh4D0ID3M4NW5fZZra9qT2Klfy8KZMN1Cpzl5QGiM%2FzPPO%2F7O2vj%2FTqVYKTKQv%2Bee0DMEHJPUD87m%2BdLymPpn2FMYvSA67Pkl6PG%2BldRxQ7qEIaBo6Vguch3no15tN79qxmP3GeINf4UF7Hmtq5jgVwvHJgOeJg%2FLwxXlP3z%2BMnFLwil2sREdjdFbW7xlfHsFWHMRdwvlGoDwTVX%2FwiQGErfkUr9i9MP9j2eQ3RSkyvker3ybyC3lhm%2FHpElqWwez2IH7wgrzArBt4uAbeYwoFT5V4r29f%2F06rFrm8E%2F5q6gvgT4i4nGvr63aOGsbR4jKciXjPZAXUplDSVTCMsdgT01gXA%2FcXdhzvBfLSt0yICrmmjv3r4RjzO8ufNmJ8KBEFKZeB1GSjM2FFL4zoLf%2F4rJzpoBM8BRbhHAeJsbrx4mbgmJRSjR9Fzgj7m6As8ArF0Lvq3gFeCau4W%2FASFIfN%2BlB42fiOsANoHqGAauS45sNXLAc0VFNY4BvTVAn9LhC9mB8gYWAMvhAglMNWTcdztXRd2ZFjmMUsqh8I5MfqqnbfceKXFFrJL0xMYdeF%2F9sdXGNBVdh06iQ6bv6TzGxlM5Vw49uyE1AhCrbgohEE6my7mxmOWuLhlot37BGDg%2Bbw%2BskBmHy9SNnr5H676A3yOcq8ahYuHSMajZ2zquPxfFzOxmBizmmojdCzM%2B5vX7DJa%2B4YcxdJlnG4KJgkIbkDvm4n%2F9TcWxyrTtPYj8jJ4%2F4TsaGrnqy7hB%2F4zh6EpZl%2BuRQcbbUoDg3KPQhA8ZB4OQP%2FY8oXK9e6EJSYdv9L1NQ9C0HIsoajRUZtU3qwilvsICX1nWSl76hkcVmLxs5D3yTq91VPMB7CmOQ%2FvHJgomdZUmCpQTZLst2dq8RpMhUCRehnM6K%2BnncvEyXxWpxfpXdyW6M%2BkXZ5JwzhqNhGgjTLLAhJO2Jjj6YuZrWOV%2FuWOcB8LJ%2BN%2FnzZkNI8lzabL0dD0PL46d2Lrk3ypUltCi63GLx%2FOTenkHyFpI2gm23MTb0S%2FRWo6HHkLN52KULpTtZ%2B%2Fl6a%2BgEBmuYltsC3YHjPlZtXVwfzaez7h%2BDANUvFYzlF1rMuAVC4%2BxkTjesONvS3RbigUVvZDEARaiKzfwM92c%2BPlAWfTlHY9zvCK9LMlUeFDTkTHCyXsvw3qoug0OcVzgpiclxCGJ%2F10ievc1kXj5CN0lw01xTH%2Fty8H8gm4KMDcleSIBh%2BH4o5CYZoIL8qz6BCLJhQdPzXnvcC1LgkGparWzxLBF1FnFlfraoNhujc7v3JgP8%2F9%2F0EA4OsNPaLujSuEeB4bWELAwUnm4W7iT1OlVSg4mCzkij6uWCdCCUpmFI%2FpWJDo3wuDcNDrPQGvAwwr6fEhjDOzdXq%2FOJzXAL7iZBO6pgNJG%2B0zvN0tJ01nqgliNdRAKV2Te1kicUp%2FlVnsRUV6llm0EPycx%2BJFd9LqRBPXjyYZwXtJqTPGF%2FDJsyvfs%2BIHlJi45q8x7%2B6NFjwvvZ%2FVjdpke5%2BTMlQEumqaMVhNSjV18fdtuLuzS6yxgdqMByukgDkf5Whdp1h30UkPurDho8vYmTnulfS5sPAtM9QVp3%2ByI4ZsXBK9uiwczq4lnbpkoe2EgcylY1IM0GngILw3P1eUz3sp33df8J3uAaqmiBC5q8GxGJVZqKqUXyA9EST72IcE%2B0C1dG4QXckmcsXntKq206dMv1ep79dyfpaUmhl9ZmUMWVvEISUr8hIe0s5QSBsckHTivWjKTYEiXba0Iaoadcpyt75lpcy0BW15KIJnn1IKDSXundGSLkNTncX%2Bvkta9eeXGYfU3MSdNFc%2BX5eSLCrspz1m0P%2FtpVR0yf2zBO7iqzt9u8m8bjB9dzJjmgl2qCGXZjEkqnFI7UO2YCyAMcKqRtZPo%2FlfCdUSK7eRLg8ST147log6StZux%2FbNYtjmug99nffikUPWG6av6MFO4HZ%2B5iaEIw1RsnCUpIBF5O8dGOQWqKO%2BYCsegTB%2BdS1KeuS0LnpdtR%2Ba5sIWMRkatFqr0V6vhpM0HG%2FnmQ6WuLJYIqDQ25DBqe45pWhFSZr5WsgLQTs2tP3KJB4TSJx0tMWrkzJLeKb78mus4QsTlI1Fr10mt7V%2BqTdsn6DWmG%2BjAq73Pcy9GhgypNaaKncu%2FGIhbC8tZ3nKEZlla5ip57tN5DwSg30cS59UEaKFV6R8UFCKvkTc2erqnoJPzQUSK4hzCKSVO8f9sJrWCWm8OACsU0A54%2BQVlK%2BSreSdk0Je9W0KEgeQ56m1f4vyrqTmF4meI%2B6p2vRnHFX1GlTjwyM6MauRFxO9r%2FsqgGHSaePz%2F2EfANSPBRmg2BtiKXX84vw%2BkPPrREgJ4Hu2b39PtqoW%2FdSao8qc68D0sn0R%2BgiSESqUUMdus29HDYkh49F%2B7ziviZh6Sm0A5hsvzUeZdcMquJTE58QxI9h1uaI1Xj%2FfRB9ODlI%2FUvuEcAtNNPH2z4SlRJdg1gL951AB8UoQynkH%2BrsjSRA0lGjrmFYR48bfzOk8PyUDNWHtr%2B1z6EpSNwgCbWl9xuXj%2FM2J90Rl2QekIlZqhVg7VtgSzEkd2VOoDGgnjTJivi1jjzMSMGxZKiZVP%2FMHVPL%2FCHNSh6DbO%2FAvveKu195VlAjzkWV74ilq4J%2B0rjFkYEm%2F5f3n%2FxVAzkG25KQhO6bRQ6rc5Z%2FN1djGm74p6OzfELNEZUhci0A3qvS4D%2BQ7DpB8bd2g%2FIzDh4%3D&__VIEWSTATEGENERATOR=E3014A82&__SCROLLPOSITIONX=0&__SCROLLPOSITIONY=0&__VIEWSTATEENCRYPTED=&__ASYNCPOST=true&";

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

                logger.debug("Could not parse side names for text={} split_length=", eventName, names.length);
                throw new ScrapperException("Could not parse side names for text=" + eventName);
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
        logger.info("Start scrapping of CrystalBet");

        try {
            List<? extends Category> result = parseCategories();
            logger.info("End scrapping of CrystalBet");

            return result;

        } catch (Exception e) {

            logger.error("Scrapping failed {}", e);
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
                .filter(elem -> elem.getLocalParentId().equals(0)  && elem.getLocalLevel().equals(1))
                .collect(Collectors.toList());

        //Add categories to each sport
        sports.stream().forEach(sport -> {

            allCategories.stream().forEach(category -> {

               if(category.getLocalParentId().equals(sport.getId()) && category.getLocalLevel().equals(2)) {
                   sport.addSubCategory(category);

                   allCategories.stream().forEach(subCategory -> {
                       if(subCategory.getLocalParentId().equals(category.getId()) && subCategory.getLocalLevel().equals(3)) {
                           category.addSubCategory(subCategory);

                           try {
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

        int subCategoryCount = 0;
        int parsedEventCount = 0;
        //Elements eventList = champ.select(".new_sport_country");

        logger.info("Categories parsed: {}", allCategories.size());
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

            logger.error("Could not get events for {} id={}", surroundingCategory.getName(), surroundingCategory.getId());
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
