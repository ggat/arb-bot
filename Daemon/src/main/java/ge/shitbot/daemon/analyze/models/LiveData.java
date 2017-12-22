package ge.shitbot.daemon.analyze.models;

import ge.shitbot.scraper.datatypes.Category;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Used to store data that was just scraped from bookies.
 * It may contain or do not contain data for particular bookie.
 *
 * Created by giga on 12/22/17.
 */
public class LiveData extends HashMap<Long, List<? extends Category>> {
}
