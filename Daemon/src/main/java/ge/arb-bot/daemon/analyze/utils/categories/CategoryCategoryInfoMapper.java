package ge.arb-bot.daemon.analyze.utils.categories;

import ge.arb-bot.core.datatypes.util.HierarchicalUtils;
import ge.arb-bot.core.datatypes.util.StreamUtils;
import ge.arb-bot.persist.exceptions.PersistException;
import ge.arb-bot.persist.models.CategoryInfo;
import ge.arb-bot.scraper.datatypes.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 12/24/17.
 */
public class CategoryCategoryInfoMapper {

    protected static Logger logger = LoggerFactory.getLogger(CategoryCategoryInfoMapper.class);

    List<Category> leftOver = new ArrayList<>();

    public List<Category> getLeftOver() {
        return leftOver;
    }

    /**
     * Map Categories to corresponding CategoryInfos
     *
     * @param categories Nested structure with parents and subcategories are expected.
     * @param categoryInfos Nested structure with parents and subcategories are expected.
     * @param bookieId passed only for logging. Does not serve any other purpose
     * @return
     */
    public List<CategoryCategoryInfoPair> map(List<? extends Category> categories, List<? extends CategoryInfo> categoryInfos, Long bookieId) {
        List<CategoryCategoryInfoPair> pairs = new ArrayList<>();

        logger.info("Start matching CategoryInfos for bookie={}", bookieId);
        logger.info("CategoryInfo count={} Category count={}", categoryInfos.size(), categories.size());

        recurse(categories, categoryInfos, pairs, bookieId);

        /*if (categories.size() != targetPairList.size()) {
            logger.warn("Not all Categories got their CategoryInfos. Category cout={}, CategoryInfo count={}",
                    categories.size(), targetPairList.size());
        }*/

        return pairs;
    }

    protected void recurse(List<? extends Category> categories, List<? extends CategoryInfo> categoryInfos, List<CategoryCategoryInfoPair> targetPairList, Long bookieId) {

        //Here there is possibility categories and categoryInfo quantities will be different.
        for (Category category : categories) {

            // FIXME: This is failing if we have same league name like Jamaica - > Premier League and England -> Premier League.
            // So we need a way to compare parents too or something like that.
            CategoryInfo categoryInfo = null;
            try {

                //FIXME: This failed for some bookies cause some bookie categories had sports as parents some had just null
                categoryInfo = categoryInfos.stream()
                        .filter(categoryInfoItem -> HierarchicalUtils.matches(categoryInfoItem, category))
                        .collect(StreamUtils.singletonCollector());
            } catch (IllegalStateException e) { // Multiple possible matches found

                //Retry, but compare more details (subCategories)
                categoryInfo = categoryInfos.stream()
                        .filter(categoryInfoItem -> HierarchicalUtils.matchesDeep(categoryInfoItem, category)).collect(StreamUtils.singletonCollector());

                System.out.println("adasd");
            }

            if (categoryInfo != null) {
                CategoryCategoryInfoPair pair = new CategoryCategoryInfoPair(category, categoryInfo);
                targetPairList.add(pair);

                recurse(category.getSubCategories(), categoryInfo.getSubCategoryInfos(), targetPairList, bookieId);

            } else {
                leftOver.add(category);
                Category parentCategory = category.getParent();
                logger.warn("Could not find CategoryInfo for Category={} and Category.id={} and Bookie.id={} and Parent={}", category.getName(), category.getId(), bookieId, parentCategory != null ? parentCategory.getName() : "Null");
            }
        }
    }
}
