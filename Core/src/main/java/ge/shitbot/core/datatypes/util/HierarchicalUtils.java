package ge.shitbot.core.datatypes.util;

import ge.shitbot.core.datatypes.Hierarchical;

import java.util.List;

/**
 * Created by giga on 1/9/18.
 */
public class HierarchicalUtils {

    private static final String NO_PARENT_NAME = "NO_PARENT";

    public static boolean matches(Hierarchical categoryInfo, Hierarchical category) {

        if(categoryInfo.getName().equals(category.getName())) {
            Hierarchical categoryInfoParent = categoryInfo.getParent();
            Hierarchical categoryParent = category.getParent();

            String categoryInfoParentName = categoryInfoParent != null ? categoryInfoParent.getName() : NO_PARENT_NAME;
            String categoryParentName = categoryParent != null ? categoryParent.getName() : NO_PARENT_NAME;

            if(categoryInfoParentName.equals(categoryParentName)) {
                return true;
            }
        }

        return false;
    }

    public static boolean matchesDeep(Hierarchical categoryInfo, Hierarchical category) {

        if(matches(categoryInfo, category)) {
            List<Hierarchical> subCatInfos = categoryInfo.getSubCategories();
            List<Hierarchical> subCats = category.getSubCategories();

            for(Hierarchical subCatInfo : subCatInfos) {
                for(Hierarchical subCat : subCats) {

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
