package ge.shitbot.daemon.mapper;

import ge.shitbot.core.datatypes.util.FileSerializer;
import ge.shitbot.core.datatypes.util.Resources;
import ge.shitbot.daemon.analyze.utils.categories.CategoryCategoryInfoMapper;
import ge.shitbot.daemon.analyze.utils.categories.CategoryCategoryInfoPair;
import ge.shitbot.daemon.util.Generator;
import ge.shitbot.persist.CategoryInfoRepository;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import org.junit.Test;

import java.net.URL;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by giga on 2/6/18.
 */
public class CategoryCategoryInfoMapperTest {

    String randomGeneratedCategoriesFile = Paths.get(System.getProperty("java.io.tmpdir"), "capitured-categories-" + UUID.randomUUID()).toString();
    long bookieId = 7L;

    /*@Test
    public void test() throws Exception {

        Long bookieId = 25L;
        List<? extends Category> categories = new AdjaraBetScraper().getFreshData();
        updateCategoryInfos(categories);

        CategoryCategoryInfoMapper mapper = new CategoryCategoryInfoMapper();
        //Flatten category
        //FlatCategories flatCategories = new FlatCategories(categories);
        CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepository();
        List<? extends CategoryInfo> categoryInfosForBookie = categoryInfoRepository.getCategoryInfosForBookie(bookieId);
        List<CategoryCategoryInfoPair> pairs = mapper.map(categories, categoryInfosForBookie, bookieId);

        System.out.println(pairs);
    }

    protected void updateCategoryInfos(List<? extends Category> categories) throws Exception {
        {
            CategoryInfoRepository repository = new CategoryInfoRepository();
            BookieRepository bookieRepository = new BookieRepository();
            String targetBookie = BookieNames.AJARA_BET;
            Long bookieId = bookieRepository.bookieIdByName(targetBookie);

            List<CategoryInfo> categoryInfos = new ArrayList<>();

            categories.stream().forEach(category -> {

                CategoryInfo parentCategoryInfo = new CategoryInfo();
                parentCategoryInfo.setName(category.getName());
                parentCategoryInfo.setBookieId(bookieId);

                // Add parent categoryInfo too
                categoryInfos.add(parentCategoryInfo);

                category.getSubCategories().forEach(subCategory -> {
                    CategoryInfo categoryInfo = new CategoryInfo();
                    categoryInfo.setName(subCategory.getName());
                    categoryInfo.setBookieId(bookieId);
                    categoryInfo.setParent(parentCategoryInfo);

                    // Add sub categoryInfo
                    categoryInfos.add(categoryInfo);
                });
            });

            repository.updateCategoryInfosForBookie(bookieId, categoryInfos);
        }
    }*/

    private List<CategoryInfo> createCategoryInfos(List<? extends Category> categories) {

        List<CategoryInfo> categoryInfos = new ArrayList<>();

        categories.stream().forEach(category -> {

            CategoryInfo parentCategoryInfo = new CategoryInfo();
            parentCategoryInfo.setName(category.getName());
            parentCategoryInfo.setBookieId(bookieId);

            // Add parent categoryInfo too
            categoryInfos.add(parentCategoryInfo);

            List<CategoryInfo> subCategoryInfos = new ArrayList<>();

            category.getSubCategories().forEach(subCategory -> {

                CategoryInfo categoryInfo = new CategoryInfo();
                categoryInfo.setName(subCategory.getName());
                categoryInfo.setBookieId(bookieId);
                categoryInfo.setParent(parentCategoryInfo);

                subCategoryInfos.add(categoryInfo);

                // Add sub categoryInfo
                //categoryInfos.add(categoryInfo);
            });

            parentCategoryInfo.setSubCategoryInfos(subCategoryInfos);
        });

        return categoryInfos;
    }

    private List<Category> createCategories(Set<String> categoryNames, Set<String> subCategoryNames, Set<String> teamNames) throws Exception {

        List<Category> categories = new ArrayList<>();
        for (String categoryName : categoryNames) {
            Category category = new Category(categoryName, new Long(Generator.randomId()));

            for (int i = 0; i < Generator.random(1, 7); i++) {
                //Take random name from subCategoriesNames and create category with it and Random name
                //So that very same names can be repeated for 1 or more categories
                category.addSubCategory(new Category(randomItem(subCategoryNames), new Long(Generator.randomId())));
            }

            categories.add(category);
        }

        return categories;
    }

    private String randomItem(Set<String> set) throws Exception {

        int size = set.size();
        int item = new Random().nextInt(size); // In real life, the Random object should be rather more shared than this
        int i = 0;
        for(String obj : set)
        {
            if (i == item)
                return obj;
            i++;
        }

        throw new Exception("Could not cpck random item.");
    }

    @Test
    public void testMapping() throws Exception {

        Set<String> categoryNames = (Set<String>)FileSerializer.loadFromResources(this, "category-names.ser");
        Set<String> subCategoryNames = (Set<String>)FileSerializer.loadFromResources(this, "subcategory-names.ser");
        Set<String> teamNames = (Set<String>)FileSerializer.loadFromResources(this, "team-names.ser");

        List<Category> categories = createCategories(categoryNames, subCategoryNames, teamNames);
        FileSerializer.toFile(randomGeneratedCategoriesFile, categories);

        List<CategoryInfo> categoryInfos = createCategoryInfos(categories);

        //Change one category's name after categoryInfos where cteated from them.
        //categories.get(0).setName(categories.get(0).getName() + "AAA");

        CategoryCategoryInfoMapper categoryCategoryInfoMapper = new CategoryCategoryInfoMapper();
        List<CategoryCategoryInfoPair> pairedOnes = categoryCategoryInfoMapper.map(categories, categoryInfos, 1L);


        assertThat(categoryCategoryInfoMapper.getLeftOver().size(), is(0));

        System.out.println("");
    }

    @Test
    public void dummy() throws Exception {

        CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepository();
        List<? extends CategoryInfo> categoryInfosForBookie = categoryInfoRepository.getCategoryInfosForBookie(bookieId);
        System.out.println("");
    }
}
