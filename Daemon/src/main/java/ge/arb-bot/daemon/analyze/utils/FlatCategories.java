package ge.arb-bot.daemon.analyze.utils;

import ge.arb-bot.scraper.datatypes.Category;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by giga on 12/21/17.
 */
public class FlatCategories extends ArrayList<Category> {

    public FlatCategories(List<? extends Category> categories) {
        recurse(categories);
    }

    protected void recurse(List<? extends Category> categories) {

        for(Category category : categories) {
            this.add(category);
            recurse(category.getSubCategories());
        }
    }
}
