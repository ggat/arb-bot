package ge.shitbot.daemon;

import ge.shitbot.daemon.analyze.utils.categories.CategoryCategoryInfoMapper;
import ge.shitbot.daemon.analyze.utils.categories.CategoryCategoryInfoPair;
import ge.shitbot.hardcode.BookieNames;
import ge.shitbot.persist.BookieRepository;
import ge.shitbot.persist.CategoryInfoRepository;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.bookies.AdjaraBetScraper;
import ge.shitbot.scraper.datatypes.Category;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 2/6/18.
 */
public class CategoryCategoryInfoMapperTest {

    @Test
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
    }
}
