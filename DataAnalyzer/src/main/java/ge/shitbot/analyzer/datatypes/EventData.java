package ge.shitbot.analyzer.datatypes;

import ge.shitbot.core.datatypes.OddType;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by giga on 12/21/17.
 */
public class EventData {

    protected Date date;
    protected String sideOne;
    protected String sideTwo;
    protected Map<OddType, Double> odds = new HashMap<>();

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getSideOne() {
        return sideOne;
    }

    public void setSideOne(String sideOne) {
        this.sideOne = sideOne;
    }

    public String getSideTwo() {
        return sideTwo;
    }

    public void setSideTwo(String sideTwo) {
        this.sideTwo = sideTwo;
    }

    public Map<OddType, Double> getOdds() {
        return odds;
    }

    public void setOdds(Map<OddType, Double> odds) {
        this.odds = odds;
    }
}
