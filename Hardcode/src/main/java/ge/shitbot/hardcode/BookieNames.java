package ge.shitbot.hardcode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 11/10/17.
 */
public class BookieNames {
    public static final String AJARA_BET    = "AdjaraBet";
    public static final String BET_LIVE     = "BetLive";
    public static final String CRYSTAL_BET  = "CrystalBet";
    public static final String EUROPE_BET   = "EuropeBet";
    public static final String LIDER_BET    = "LiderBet";
    public static final String CROCO_BET    = "CrocoBet";

    public static List<String> asList() {

        ArrayList<String> list = new ArrayList<>();

        list.add(AJARA_BET);
        list.add(BET_LIVE);
        list.add(CRYSTAL_BET);
        list.add(EUROPE_BET);
        list.add(LIDER_BET);
        list.add(CROCO_BET);

        return list;

    }
}
