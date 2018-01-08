package ge.shitbot.persist.tests;

import ge.shitbot.persist.CategoryInfoRepository;
import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.persist.models.Person;
import ge.shitbot.persist.models.Ranking;
import ge.shitbot.persist.models.Skill;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by giga on 12/7/17.
 */
public class CategoryInfoTest {

    CategoryInfoRepository repository;

    @Before
    public void setup() throws Exception {
        repository = new CategoryInfoRepository();
    }

    @Test
    public void testFetch() {
        List<? extends CategoryInfo> categoryInfos = repository.getCategoryInfos();

        System.out.println("List length: " + categoryInfos.size());

        assertNotEquals(0, categoryInfos.size());
    }

    @Test
    public void testStorage() {

        CategoryInfo info = new CategoryInfo();
        info.setName("TestCategoryInfo");
        info.setBookieId(25L);

        repository.saveCategoryInfo(info);

        assertNotNull(repository.byName("TestCategoryInfo"));
    }

    @Test
    public void testUpdateForBookie() {

        Long bookieId = 25L;
        List<CategoryInfo> categoryInfos = new ArrayList<>();

        CategoryInfo info = new CategoryInfo();
        info.setName("TestCategoryInfo");
        info.setBookieId(bookieId);

        categoryInfos.add(info);

        repository.updateCategoryInfosForBookie(bookieId, categoryInfos);

        /*info = new CategoryInfo();
        info.setName("TestCategoryInfo");
        info.setBookieId(bookieId);

        categoryInfos.add(info);*/

        repository.updateCategoryInfosForBookie(bookieId, categoryInfos);
        repository.updateCategoryInfosForBookie(bookieId, categoryInfos);

        assertEquals(1, repository.getCategoryInfosByName("TestCategoryInfo").size());
    }

    @Test
    public void childrenTest() {

        List<? extends CategoryInfo> infos = repository.categoryInfosWithChildren();

        System.out.println("CategoryInfos with children size: " + infos.size());

        if(infos.size() > 0 ) {
            for (CategoryInfo info : infos) {
                assertNotNull(info.getParent());
            }
        }
    }
}
