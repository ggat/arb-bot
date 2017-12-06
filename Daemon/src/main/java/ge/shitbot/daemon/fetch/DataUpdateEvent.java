package ge.shitbot.daemon.fetch;

import ge.shitbot.scraper.datatypes.Category;

import java.util.HashMap;
import java.util.List;

/**
 * Created by giga on 12/6/17.
 */
public class DataUpdateEvent {

    List<? extends Category> data;

    public DataUpdateEvent(List<? extends Category> data) {
        this.data = data;
    }

    public List<? extends Category> getData() {
        return data;
    }

    public void setData(List<? extends Category> data) {
        this.data = data;
    }
}
