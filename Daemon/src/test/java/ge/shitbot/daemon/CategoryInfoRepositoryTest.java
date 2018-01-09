package ge.shitbot.daemon;

import ge.shitbot.daemon.analyze.AnalyzerService;
import ge.shitbot.daemon.analyze.models.LiveData;
import ge.shitbot.daemon.exceptions.AnalyzeException;
import ge.shitbot.persist.CategoryInfoRepository;
import ge.shitbot.persist.ChainRepository;
import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.datatypes.Category;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by giga on 12/7/17.
 */

//TODO: This test class should be part of Persist module not this module.
public class CategoryInfoRepositoryTest {

    Long ID = 1L;
    private Long getNextId() {
        return ID++;
    }

    private final String savedDataFile = "freshData.tmp";

    @Test
    public void testArbSearch() throws IOException, ClassNotFoundException, PersistException, AnalyzeException {

        CategoryInfo categoryInfoDinamo = new CategoryInfo();
        categoryInfoDinamo.setName("Dinamo");
        categoryInfoDinamo.setBookieId(25L);
        CategoryInfo categoryInfoMinnesota = new CategoryInfo();
        categoryInfoMinnesota.setName("Minnesota");
        categoryInfoMinnesota.setBookieId(25L);

        CategoryInfo categoryInfoTbilisi = new CategoryInfo();
        categoryInfoTbilisi.setName("Tbilisi");
        categoryInfoTbilisi.setBookieId(25L);

        categoryInfoDinamo.setParent(categoryInfoTbilisi);
        categoryInfoDinamo.setParent(categoryInfoMinnesota);

        CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepository();

        List<CategoryInfo> categoryInfos = new ArrayList<>();
        categoryInfos.add(categoryInfoTbilisi);

        categoryInfoRepository.updateCategoryInfosForBookie(30L, categoryInfos);

        assertNotNull(categoryInfoRepository.byName("Dinamo"));

    }

    @Test
    public void testSaveFromCapture() throws Exception {
        FileInputStream fis = new FileInputStream("../capture.tmp");
        ObjectInputStream ois = new ObjectInputStream(fis);
        Category category = (Category) ois.readObject();
        ois.close();

        Long bookieId = 29L;

        List<Category> data = new ArrayList<>();
        data.add( category);
        List<CategoryInfo> categoryInfos = new ArrayList<>();

        data.stream().forEach(categoryItem -> {

            CategoryInfo parentCategoryInfo = new CategoryInfo();
            parentCategoryInfo.setName(categoryItem.getName());
            parentCategoryInfo.setBookieId(bookieId);

            System.out.println("ss");

            // Add parent categoryInfo too
            categoryInfos.add(parentCategoryInfo);

            categoryItem.getSubCategories().forEach(subCategory -> {
                CategoryInfo categoryInfo = new CategoryInfo();
                categoryInfo.setName(subCategory.getName());
                categoryInfo.setBookieId(bookieId);
                categoryInfo.setParent(parentCategoryInfo);

                System.out.println("ss");

                // Add sub categoryInfo
                categoryInfos.add(categoryInfo);
            });
        });

        CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepository();

        categoryInfoRepository.updateCategoryInfosForBookie(bookieId, categoryInfos);

        System.out.println("asdsad");
    }
}
