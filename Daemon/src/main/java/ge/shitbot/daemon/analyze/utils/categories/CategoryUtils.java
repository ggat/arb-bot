package ge.shitbot.daemon.analyze.utils.categories;

import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.datatypes.Category;

import java.util.List;

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

    public static boolean matchesDeep(CategoryInfo categoryInfo, Category category) {

        if(matches(categoryInfo, category)) {
            List<CategoryInfo> subCatInfos = categoryInfo.getSubCategoryInfos();
            List<Category> subCats = category.getSubCategories();

            for(CategoryInfo subCatInfo : subCatInfos) {
                for(Category subCat : subCats) {

                    //If any of subCategoryNames match
                    //Parent comparison makes no sense here as we know that they have same parents, for sure.
                    if(subCatInfo.getName().equals(subCat.getName())){
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
