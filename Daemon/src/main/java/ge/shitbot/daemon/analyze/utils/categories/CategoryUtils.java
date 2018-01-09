package ge.shitbot.daemon.analyze.utils.categories;

import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.datatypes.Category;

/**
 * Created by giga on 1/9/18.
 */
public class CategoryUtils {

    private static final String NO_PARENT_NAME = "NO_PARENT";

    public static boolean matches(CategoryInfo categoryInfo, Category category) {

        if(categoryInfo.getName().equals(category.getName())) {
            CategoryInfo categoryInfoParent = categoryInfo.getParent();
            Category categoryParent = category.getParent();

            String categoryInfoParentName = categoryInfoParent != null ? categoryInfoParent.getName() : NO_PARENT_NAME;
            String categoryParentName = categoryParent != null ? categoryParent.getName() : NO_PARENT_NAME;

            if(categoryInfoParentName.equals(categoryParentName)) {
                return true;
            }
        }

        return false;
    }
}
