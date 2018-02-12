package ge.shitbot.daemon.util;

import ge.shitbot.daemon.analyze.utils.categories.CategoryCategoryInfoPair;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.datatypes.Category;

import java.util.Random;

/**
 * Created by giga on 2/9/18.
 */
public class Generator {

    public static Integer randomId() {
        return random(1, 5000);
    }

    public static Integer random(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    public static Category randomCategory() {
        int random = randomId();
        return new Category("Category " + random, new Long(random));
    }

    public static CategoryInfo randomCategoryInfo() {
        int random = randomId();
        CategoryInfo categoryInfo = new CategoryInfo();

        categoryInfo.setId(new Long(random));
        categoryInfo.setName("CategoryInfo " + random);

        return categoryInfo;
    }

    public static CategoryCategoryInfoPair randomPair() {
        return randomCategoryCategoryInfoPair();
    }

    public static CategoryCategoryInfoPair randomCategoryCategoryInfoPair() {

        int random = randomId();
        int randomInfo = randomId();

        String categoryName = "Category " + random;
        String categoryInfoName = "CategoryInfo " + randomInfo;

        Category category = new Category(categoryName, new Long(random));
        CategoryInfo categoryInfo = new CategoryInfo();

        categoryInfo.setId(new Long(randomInfo));
        categoryInfo.setName(categoryInfoName);

        return new CategoryCategoryInfoPair(category, categoryInfo);
    }
}
