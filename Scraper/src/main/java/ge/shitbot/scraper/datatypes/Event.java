package ge.shitbot.scraper.datatypes;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by giga on 11/21/17.
 */
public class Event
{
    private Category category;
    private Date date;
    private String sideOne;
    private String sideTwo;
    private Map<String, Double> odds = new HashMap<>();

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

    public Map<String, Double> getOdds() {
        return odds;
    }

    public void setOdds(Map<String, Double> odds) {
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
