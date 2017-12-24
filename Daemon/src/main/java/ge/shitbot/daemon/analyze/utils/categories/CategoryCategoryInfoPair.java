package ge.shitbot.daemon.analyze.utils.categories;

import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.datatypes.Category;

/**
 * Created by giga on 12/24/17.
 */
public class CategoryCategoryInfoPair {

    private Category category;
    private CategoryInfo categoryInfo;

    public Category getCategory() {
        return category;
    }

    public CategoryInfo getCategoryInfo() {
        return categoryInfo;
    }

    public CategoryCategoryInfoPair(Category category, CategoryInfo categoryInfo) {
        this.category = category;
        this.categoryInfo = categoryInfo;
    }

}