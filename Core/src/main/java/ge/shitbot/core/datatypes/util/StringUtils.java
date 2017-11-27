package ge.shitbot.core.datatypes.util;

import java.io.InputStream;
import java.util.Scanner;

/**
 * Created by giga on 11/27/17.
 */
public class StringUtils {

    public static String fromStream(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }
}
