package ge.shitbot.core.datatypes.util;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;

/**
 * Created by giga on 2/12/18.
 */
public class Resources {

    public static URL loadFromRoot(Object referer, String resource){
        return referer.getClass().getClassLoader().getResource(resource);
    }

    public static URL loadFromSamePackage(Object referer, String resource){
        return referer.getClass().getResource(resource);
    }

    public static String toAbsName(URL url) throws URISyntaxException {
        return Paths.get(url.toURI()).toAbsolutePath().toString();
    }
}
