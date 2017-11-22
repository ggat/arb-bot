package ge.shitbot.scraper.bookies;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ge.shitbot.scraper.datatypes.Category;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by giga on 11/22/17.
 */
public class AdjaraBetScraper {

    private static Logger logger = LoggerFactory.getLogger(CrystalBetScraper.class);

    protected static class AdjaraNames {
        public String Category;
        public String CategoryID;
        public String Group;
        public String GroupID;
        public String SubGroup;
        public String SubGroupID;
    }

    protected static class L {
        public int id;
        public String n;
        public int nb;
    }

    protected static class C {
        public int id;
        public String n;
        public String c;
        public int nb;
        public int p;
        public List<L> l;
    }

    protected static class S {
        public int priority;
        public int id;
        public String n;
        public int nb;
        public String s;
        public List<C> c;
    }

    protected static class AdjarabetTree {
        public List<Integer> f;
        public List<S> s;
    }

    public AdjaraBetScraper() {

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
        String sportBookTree = executeGet("https://bookmakersapi.adjarabet.com/sportsbook/rest/public/sportbookTree?ln=ka");

        ObjectMapper mapper = new ObjectMapper();

        AdjarabetTree adjarabetTree = mapper.readValue(sportBookTree, AdjarabetTree.class);

        System.out.println("FFF");
        return new ArrayList<>();
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
