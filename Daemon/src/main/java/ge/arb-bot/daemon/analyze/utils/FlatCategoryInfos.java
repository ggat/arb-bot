package ge.arb-bot.daemon.analyze.utils;

import ge.arb-bot.persist.models.CategoryInfo;
import ge.arb-bot.scraper.datatypes.Category;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 12/21/17.
 */
public class FlatCategoryInfos extends ArrayList<CategoryInfo> {

    public FlatCategoryInfos(List<? extends CategoryInfo> categories) {
        recurse(categories);
    }

    protected void recurse(List<? extends CategoryInfo> categories) {

        for(CategoryInfo category : categories) {
            this.add(category);
            recurse(category.getSubCategoryInfos());
        }
    }
}
