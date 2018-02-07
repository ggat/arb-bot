package ge.shitbot.daemon;

import ge.shitbot.core.datatypes.util.HierarchicalUtils;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.datatypes.Category;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by giga on 12/7/17.
 */
public class CategoryUtilsTest {

    Long ID = 1L;
    private Long getNextId() {
        return ID++;
    }

    @Test
    public void testMatches() {

        //Category
        //Tbilisi - Dinamo
        Category categoryDinamo = new Category("Dinamo", getNextId());
        Category categoryTbilisi = new Category("Tbilisi", getNextId());
        categoryTbilisi.addSubCategory(categoryDinamo);

        //CategoryInfo
        //Tbilisi - Dinamo
        CategoryInfo categoryInfoDinamo = new CategoryInfo();
        categoryInfoDinamo.setName("Dinamo");
        CategoryInfo categoryInfoTbilisi = new CategoryInfo();
        categoryInfoTbilisi.setName("Tbilisi");
        categoryInfoDinamo.setParent(categoryInfoTbilisi);

        //Category
        //Jamaica - Dinamo
        Category categoryDinamoButJamaica = new Category("Dinamo", getNextId());
        Category categoryJamaica = new Category("Jamaica", getNextId());
        categoryJamaica.addSubCategory(categoryDinamoButJamaica);

        //CategoryInfo
        //Jamaica - Dinamo
        CategoryInfo categoryInfoDinamoButJamaica = new CategoryInfo();
        categoryInfoDinamoButJamaica.setName("Dinamo");
        CategoryInfo categoryInfoJamaica = new CategoryInfo();
        categoryInfoJamaica.setName("Jamaica");
        categoryInfoDinamoButJamaica.setParent(categoryInfoJamaica);

        assertTrue("Child comparison failed", HierarchicalUtils.matches(categoryInfoDinamo, categoryDinamo));
        assertTrue("Parents comparison failed", HierarchicalUtils.matches(categoryInfoTbilisi, categoryTbilisi));
        assertFalse("Parent and child compared they should not match", HierarchicalUtils.matches(categoryInfoDinamo, categoryTbilisi));
        assertFalse("Parent and child compared they should not match", HierarchicalUtils.matches(categoryInfoTbilisi, categoryDinamo));

        //Now test if same names and different parents does not match.
        assertTrue(HierarchicalUtils.matches(categoryInfoDinamoButJamaica, categoryDinamoButJamaica));

        //This should fail because category names are same but parents are different, so they are different categories
        assertFalse(HierarchicalUtils.matches(categoryInfoDinamoButJamaica, categoryDinamo));
    }
}
