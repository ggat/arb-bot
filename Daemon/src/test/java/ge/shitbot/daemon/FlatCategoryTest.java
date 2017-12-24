package ge.shitbot.daemon;

import ge.shitbot.daemon.analyze.utils.FlatCategories;
import ge.shitbot.scraper.datatypes.Category;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import static org.junit.Assert.*;

/**
 * Created by giga on 12/7/17.
 */
public class FlatCategoryTest {

    private int totalCategories = 100;
    private int minSubCategories = 0;
    private int maxSubCategories = 7;

    @Test
    public void testFlatCategory() {

        List<Category> categories = new ArrayList<>();
        int subCategoriesCreated = 0;

        for(int i = 0; i < totalCategories; i++) {
            Category category = new Category("Category_" + i, new Long(i));

            int subCategoryCount = ThreadLocalRandom.current().nextInt(minSubCategories, maxSubCategories + 1);

            for(int j = 0; j < subCategoryCount; j++) {
                Category subCategory = new Category("SubCategory_" + i, new Long(i));

                category.addSubCategory(subCategory);
                subCategoriesCreated++;
            }

            categories.add(category);
        }

        FlatCategories flatCategories = new FlatCategories(categories);
        assertEquals(totalCategories + subCategoriesCreated, flatCategories.size());
    }
}
