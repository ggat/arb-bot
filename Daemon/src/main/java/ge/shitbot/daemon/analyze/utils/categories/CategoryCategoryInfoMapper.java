package ge.shitbot.daemon.analyze.utils.categories;

import ge.shitbot.core.datatypes.util.StreamUtils;
import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.datatypes.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by giga on 12/24/17.
 */
public class CategoryCategoryInfoMapper {

    protected static Logger logger = LoggerFactory.getLogger(CategoryCategoryInfoMapper.class);

    public List<CategoryCategoryInfoPair> map(List<? extends Category> categories, List<? extends CategoryInfo> categoryInfos, Long bookieId) throws PersistException {
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
            CategoryInfo categoryInfo = categoryInfos.stream()
                    .filter(categoryInfoItem ->  categoryInfoItem.getName().equals(category.getName()))
                    .collect(StreamUtils.singletonCollector());

            if (categoryInfo != null) {
                CategoryCategoryInfoPair pair = new CategoryCategoryInfoPair(category, categoryInfo);
                targetPairList.add(pair);

                recurse(category.getSubCategories(), categoryInfo.getSubCategoryInfos(), targetPairList, bookieId);

            } else {
                logger.warn("Could not find CategoryInfo for Category={} and Category.id={} and Bookie.id={}", category.getName(), category.getId(), bookieId);
            }
        }
    }
}
