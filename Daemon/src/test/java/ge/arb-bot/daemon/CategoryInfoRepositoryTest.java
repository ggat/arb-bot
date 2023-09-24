package ge.arb-bot.daemon;

import ge.arb-bot.daemon.analyze.AnalyzerService;
import ge.arb-bot.daemon.analyze.models.LiveData;
import ge.arb-bot.daemon.exceptions.AnalyzeException;
import ge.arb-bot.persist.CategoryInfoRepository;
import ge.arb-bot.persist.ChainRepository;
import ge.arb-bot.persist.exceptions.PersistException;
import ge.arb-bot.persist.models.CategoryInfo;
import ge.arb-bot.scraper.datatypes.Category;
import org.junit.Test;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.*;

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
    // This test must be run on clean DB
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

            // Add parent categoryInfo too
            categoryInfos.add(parentCategoryInfo);

            categoryItem.getSubCategories().forEach(subCategory -> {
                CategoryInfo categoryInfo = new CategoryInfo();
                categoryInfo.setName(subCategory.getName());
                categoryInfo.setBookieId(bookieId);
                categoryInfo.setParent(parentCategoryInfo);

                // Add sub categoryInfo
                categoryInfos.add(categoryInfo);
            });
        });

        CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepository();

        categoryInfoRepository.updateCategoryInfosForBookie(bookieId, categoryInfos);

        List<CategoryInfo> secondCategoryInfos = new ArrayList<>();
        data.stream().forEach(categoryItem -> {

            CategoryInfo parentCategoryInfo = new CategoryInfo();
            parentCategoryInfo.setName(categoryItem.getName());
            parentCategoryInfo.setBookieId(bookieId);

            System.out.println("ss");

            // Add parent categoryInfo too
            secondCategoryInfos.add(parentCategoryInfo);

            categoryItem.getSubCategories().forEach(subCategory -> {
                CategoryInfo categoryInfo = new CategoryInfo();
                categoryInfo.setName(subCategory.getName());
                categoryInfo.setBookieId(bookieId);
                categoryInfo.setParent(parentCategoryInfo);

                System.out.println("ss");

                // Add sub categoryInfo
                secondCategoryInfos.add(categoryInfo);
            });

            secondCategoryInfos.add(makeSampleCategoryInfo(parentCategoryInfo, bookieId));
        });

        categoryInfoRepository.updateCategoryInfosForBookie(bookieId, secondCategoryInfos);

        assertEquals(1, categoryInfoRepository.byNameList(secondCategoryInfos.get(0).getName()).size());
    }

    private CategoryInfo makeSampleCategoryInfo(CategoryInfo parentCategoryInfo, Long bookieId){
        CategoryInfo categoryInfo = new CategoryInfo();
        categoryInfo.setName("Added subCategory");
        categoryInfo.setBookieId(bookieId);
        categoryInfo.setParent(parentCategoryInfo);

        return categoryInfo;
    }

    private CategoryInfo makeSampleCategoryInfo(String name, Long bookieId){
        CategoryInfo categoryInfo = new CategoryInfo();
        categoryInfo.setName(name);
        categoryInfo.setBookieId(bookieId);
        //categoryInfo.setParent(parentCategoryInfo);

        return categoryInfo;
    }

    @Test
    public void testByNameList() throws PersistException {

        String categoryName = UUID.randomUUID().toString();

        CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepository();
        CategoryInfo categoryInfo1 = makeSampleCategoryInfo(categoryName, 29L);
        CategoryInfo categoryInfo2 = makeSampleCategoryInfo(categoryName, 29L);

        categoryInfoRepository.saveCategoryInfo(categoryInfo1);
        categoryInfoRepository.saveCategoryInfo(categoryInfo2);

        assertEquals(2, categoryInfoRepository.byNameList(categoryName).size());
    }

    @Test
    public void testCascade() throws PersistException {

        CategoryInfo parent = new CategoryInfo();
        parent.setName("Parent");
        parent.setBookieId(27L);
        //parent.setParent(parentCategoryInfo);

        CategoryInfo child = new CategoryInfo();
        child.setName("Child");
        child.setBookieId(27L);
        child.setParent(parent);

        CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepository();
        categoryInfoRepository.saveCategoryInfo(child);
        //categoryInfoRepository.saveCategoryInfo(parent);
    }
}
