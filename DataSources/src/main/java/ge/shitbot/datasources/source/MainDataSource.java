package ge.shitbot.datasources.source;

import com.fasterxml.jackson.databind.ObjectMapper;
import ge.shitbot.datasources.datatypes.Arb;
import com.fasterxml.jackson.core.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by giga on 9/27/17.
 */
public class MainDataSource {


    public static void main(String[] args) throws Exception {
        (new MainDataSource()).run();
    }

    protected void run() throws Exception {

        ObjectMapper mapper = new ObjectMapper();

        String value = "{\n" +
                "    \"profit\": 0.50916496945009726,\n" +
                "    \"date\": \"28 Sep 2017 23:05\",\n" +
                "    \"hostID\": 11553,\n" +
                "    \"guestID\": 11554,\n" +
                "    \"bookie_1\": {\n" +
                "      \"name\": \"BetLive\",\n" +
                "      \"odd_type\": \"1\",\n" +
                "      \"odd\": 3.5,\n" +
                "      \"team_1_name\": \"შერიფი\",\n" +
                "      \"team_2_name\": \"კოპენჰაგენი\",\n" +
                "      \"category\": \"UEFA\",\n" +
                "      \"sub_category\": \"ევროპის ლიგა\"\n" +
                "    },\n" +
                "    \"bookie_2\": {\n" +
                "      \"name\": \"CrocoBet\",\n" +
                "      \"odd_type\": \"X2\",\n" +
                "      \"odd\": 1.41,\n" +
                "      \"team_1_name\": \"\",\n" +
                "      \"team_2_name\": \"\",\n" +
                "      \"category\": \"UEFA\",\n" +
                "      \"sub_category\": \"ევროპის ლიგა\"\n" +
                "    }}";

        value = getRawData();

        List<Arb> arbs = mapper.readValue(value, new TypeReference<List<Arb>>(){});
        //Arb arb = mapper.readValue(value, Arb.class);

        System.out.println(arbs.size());
    }

    protected String getRawData() throws IOException, URISyntaxException {

        Path path = Paths.get("/home/giga/Projects/ShitBot/DataSources/src/main/java/ge/shitbot/datasources/source/Arb.json");

        return new String(Files.readAllBytes(path));
    }
}
