package ge.shitbot.persist;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Created by giga on 12/7/17.
 */
public class Event {

    Long id;
    Timestamp date;
    String title;

    public Event() {
    }

    public Event(Timestamp date, String title) {
        this.date = date;
        this.title = title;
    }

    public Event(String title) {
        this.date = new Timestamp(System.currentTimeMillis());
        this.title = title;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
