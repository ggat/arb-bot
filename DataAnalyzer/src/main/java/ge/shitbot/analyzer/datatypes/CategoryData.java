package ge.shitbot.analyzer.datatypes;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 12/21/17.
 */
public class CategoryData {

    protected String bookieName;
    protected String category;
    protected String subCategory;
    protected List<EventData> events = new ArrayList<>();

    public String getBookieName() {
        return bookieName;
    }

    public void setBookieName(String bookieName) {
        this.bookieName = bookieName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public List<EventData> getEvents() {
        return events;
    }

    public void setEvents(List<EventData> events) {
        this.events = events;
    }

    @Override
    public String toString() {
        return "CategoryData{" +
                "bookieName='" + bookieName + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
