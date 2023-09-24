package ge.arb-bot.scraper.datatypes;

import ge.arb-bot.core.datatypes.OddType;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by giga on 11/21/17.
 */
public class Event implements Serializable
{
    private Category category;
    protected Date date;
    protected String sideOne;
    protected String sideTwo;
    private Map<OddType, Double> odds = new HashMap<>();

    public Event() {
    }

    public Event(Category category, Date date, String sideOne, String sideTwo) {
        this.category = category;
        this.date = date;
        this.setSideOne(sideOne);
        this.setSideTwo(sideTwo);
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

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
        this.sideOne = sideOne != null ? sideOne.trim() : null;
    }

    public String getSideTwo() {
        return sideTwo;
    }

    public void setSideTwo(String sideTwo) {
        this.sideTwo = sideTwo != null ? sideTwo.trim() : null;
    }

    public Map<OddType, Double> getOdds() {
        return odds;
    }

    public void setOdds(Map<OddType, Double> odds) {
        this.odds = odds;
    }

    @Override
    public String toString() {
        return "Event{" +
                "category=" + category +
                ", date=" + date +
                ", sideOne='" + sideOne + '\'' +
                ", sideTwo='" + sideTwo + '\'' +
                ", odds=" + odds +
                '}';
    }
}
