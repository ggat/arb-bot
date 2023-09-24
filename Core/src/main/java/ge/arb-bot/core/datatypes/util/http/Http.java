package ge.arb-bot.core.datatypes.util.http;

import ge.arb-bot.core.datatypes.util.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by giga on 11/27/17.
 */
public class Http {

    private static Logger logger = LoggerFactory.getLogger(Http.class);

    public static String get(String url) throws IOException {
        return get(url, new HashMap<>());
    }

    public static String get(String url, Map<String, String> headers) throws IOException {
        HttpGet get = null;
        try {
            get = new HttpGet(new URI(url));
        } catch (URISyntaxException e) {

            logger.error("Bad URI syntax: " + e);
            e.printStackTrace();
        }

        for (Map.Entry<String, String> header : headers.entrySet()) {
            get.setHeader(header.getKey(), header.getValue());
        }

        //get.setHeader("Request-Language", "en");

        HttpClient client = HttpClients.createDefault();
        HttpResponse response = client.execute(get);
        InputStream inputStream = response.getEntity().getContent();

        return StringUtils.fromStream(inputStream);
    }

    public static String post(String url, Map<String, String> headers, String payload) throws Exception {
        CloseableHttpClient client = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(url);

        for (Map.Entry<String, String> header : headers.entrySet()) {
            httpPost.setHeader(header.getKey(), header.getValue());
        }
        
        httpPost.setEntity(new StringEntity(payload));

        //httpPost.setRe(new StringRequestEntity(stringData));

        /*List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", "John"));
        params.add(new BasicNameValuePair("password", "pass"));
        httpPost.setEntity(new UrlEncodedFormEntity(params));*/

        CloseableHttpResponse response = client.execute(httpPost);
        String responseBody = StringUtils.fromStream(response.getEntity().getContent());
        client.close();

        return responseBody;
    }

}
