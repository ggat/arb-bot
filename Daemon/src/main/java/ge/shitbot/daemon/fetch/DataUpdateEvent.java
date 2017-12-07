package ge.shitbot.daemon.fetch;

import ge.shitbot.scraper.datatypes.Category;

import java.util.HashMap;
import java.util.List;

/**
 * Created by giga on 12/6/17.
 */
public class DataUpdateEvent {

    List<? extends Category> data;
    String target;

    public DataUpdateEvent(List<? extends Category> data, String target) {
        this.data = data;
        this.target = target;
    }

    public List<? extends Category> getData() {
        return data;
    }

    public void setData(List<? extends Category> data) {
        this.data = data;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }
}
