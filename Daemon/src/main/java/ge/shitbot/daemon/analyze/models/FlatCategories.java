package ge.shitbot.daemon.analyze.models;

import ge.shitbot.scraper.datatypes.Category;

import java.util.ArrayList;
import java.util.List;

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
