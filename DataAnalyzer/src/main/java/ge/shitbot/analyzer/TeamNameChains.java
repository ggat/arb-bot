package ge.shitbot.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by giga on 2/16/18.
 */
public class TeamNameChains extends ArrayList<Map<String, String>> {

    public Map<String, String> findFirst(String bookieName, String teamName) {
        for (Map<String,String> stringStringHashMap : this) {
            if(stringStringHashMap.get(bookieName) != null && stringStringHashMap.get(bookieName).equals(teamName)) {
                return stringStringHashMap;
            }
        }

        return null;
    }

    public Map<String, String> findFirst(String teamName) {
        for (Map<String,String> stringStringHashMap : this) {

            for (String teamElem : stringStringHashMap.values()) {
                if(teamElem.equals(teamName)) {
                    return stringStringHashMap;
                }
            }
        }

        return null;
    }
}
