package ge.shitbot.datasources.source;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.datasources.exceptions.DataSourceException;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by giga on 9/27/17.
 */
public class NewDataSource<T extends Arb> implements ArbDataSource {

    Logger logger = LoggerFactory.getLogger(NewDataSource.class);

    public static void main(String[] args) throws Exception {
        (new NewDataSource()).getArbs();
    }

    public String getRawData() throws DataSourceException {

        return fromHttpService();
    }

    private String fromFile() throws DataSourceException {
        Path path = Paths.get("/home/giga/Projects/ShitBot/DataSources/src/main/java/ge/shitbot/datasources/source/Arb.json");

        String data = null;
        try {
            data = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            throw new DataSourceException(e);
        }

        return data;
    }

    private String fromHttpService() throws DataSourceException {

        CloseableHttpClient httpClient = HttpClients.createDefault();
        ObjectMapper mapper = new ObjectMapper();

        HttpGet request = new HttpGet("http://homestead.test/arb-infos");

        //request.setEntity(new StringEntity(mapper.writeValueAsString(params)));

        request.setHeader(HttpHeaders.ACCEPT, "application/json");

        try {
            HttpResponse response = httpClient.execute(request);

            String rawData = IOUtils.toString(response.getEntity().getContent());
            JsonNode rootNode = mapper.readTree(rawData);

            JsonNode status = rootNode.get("status");

            if(status != null && status.asText().equals("OK")) {
                JsonNode data = rootNode.get("data");
                return data.asText();
            }

            logger.warn("Unsuccessful response from remote server, trying to get list of Arbs");
            throw new IOException("Unsuccessful response from remote server, trying to get list of Arbs");

        } catch (IOException e) {
            throw new DataSourceException(e);
        }
    }

    public List<T> getArbs() throws DataSourceException {
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


        List<T> arbs = null;
        try {
            value = getRawData();

            arbs = mapper.readValue(value, new TypeReference<List<T>>(){});
        } catch (Exception e) {
            throw ((e instanceof DataSourceException) ? (DataSourceException) e :
                    new DataSourceException("Could not get data.", e));
        }

        System.out.println(arbs.size());

        return arbs;
    }
}
