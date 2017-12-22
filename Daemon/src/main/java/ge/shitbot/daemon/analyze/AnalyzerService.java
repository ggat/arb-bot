package ge.shitbot.daemon.analyze;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ge.shitbot.analyzer.Analyzer;
import ge.shitbot.analyzer.datatypes.CategoryData;
import ge.shitbot.analyzer.datatypes.ComparableChain;
import ge.shitbot.analyzer.datatypes.EventData;
import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.core.datatypes.util.StreamUtils;
import ge.shitbot.daemon.analyze.models.Chain;
import ge.shitbot.daemon.analyze.models.FlatCategories;
import ge.shitbot.daemon.analyze.models.LiveData;
import ge.shitbot.daemon.analyze.utils.ChainUtils;
import ge.shitbot.daemon.exceptions.AnalyzeException;
import ge.shitbot.persist.CategoryInfoRepository;
import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.models.Bookie;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by giga on 12/21/17.
 */
public class AnalyzerService {

    Logger logger = LoggerFactory.getLogger(AnalyzerService.class);

    protected class Pair {
        private Category category;
        private CategoryInfo categoryInfo;

        public Category getCategory() {
            return category;
        }

        public CategoryInfo getCategoryInfo() {
            return categoryInfo;
        }

        public Pair(Category category, CategoryInfo categoryInfo) {
            this.category = category;
            this.categoryInfo = categoryInfo;
        }
    }

    /**
     *
     * @param chains
     * @return
     */

    //FIXME: Bookie names here are passed only to get name of bookie from id.
    public List<ComparableChain> createComparableChains(LiveData liveData,
                                                        List<Chain> chains, Map<Long, String> bookieNames) throws AnalyzeException {
        List<ComparableChain> comparableChains = new ArrayList<>();

        try {
            // Loop over each chain. Each chain is going to translate to ComparableChain
            // Conversion Chain -> ComparableChain
            for (Chain chain : chains) {

                ComparableChain comparableChain = new ComparableChain();

                // Here
                // Entry.key    - bookieId
                // Entry.value  - categoryId
                for (Map.Entry<Long, Long> chainItem : chain.entrySet()) {

                    Long bookieId = chainItem.getKey();
                    Long categoryId = chainItem.getValue();

                    List<? extends Category> categories = liveData.get(bookieId);

                    if(categories == null) {
                        logger.debug("There was no data for bookieId={} skipping..", bookieId);
                        continue;
                    }

                    //Flatten category
                    FlatCategories flatCategories = new FlatCategories(categories);
                    List<Pair> pairs = mapCategoryInfosToCategories(flatCategories, bookieId);

                    // Check if value of this chainItem matches any Category of this bookie.
                    // Chain can include values for any bookie. So here chainItem may not be for targetBookie
                    // If chainItem is for targetBookie, it must find referenced category, or chain is corrupt.

                    Pair found = pairs.stream()
                            .filter(pair -> pair.getCategoryInfo().getId().equals(categoryId))
                            .collect(StreamUtils.singletonCollector());

                    if (found != null) {

                        Category foundCategory = found.getCategory();
                        CategoryInfo foundCategoryInfo = found.getCategoryInfo();

                        //TODO: Look at me.
                        //FIXME: This will not work if there is subCategory of subCatgory
                        //FIXME: It would be much better solution if we checked if subCategory have events directly
                        //Here we check if found category and if category is subCategory

                        //if(found.getParent() != null) {
                        if (foundCategory.getEvents().size() > 0) {
                            CategoryData categoryData = new CategoryData();
                            categoryData.setBookieName(bookieNames.get(bookieId));
                            categoryData.setCategory(foundCategory.getParent().getName());
                            categoryData.setSubCategory(foundCategory.getName());

                            for (Event event : foundCategory.getEvents()) {
                                EventData eventData = new EventData();
                                eventData.setDate(event.getDate());
                                eventData.setOdds(event.getOdds());
                                eventData.setSideOne(event.getSideOne());
                                eventData.setSideTwo(event.getSideTwo());

                                categoryData.getEvents().add(eventData);
                            }

                            comparableChain.add(categoryData);
                        }
                    }
                }
            }

        } catch (PersistException e) {
            throw new AnalyzeException(e);
        }

        return comparableChains;
    }

    protected List<Pair> mapCategoryInfosToCategories(List<? extends Category> categories, Long bookieId) throws PersistException {

        CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepository();

        List<Pair> pairs = new ArrayList<>();
        List<? extends CategoryInfo> categoryInfosForBookie = categoryInfoRepository.getCategoryInfosForBookie(bookieId);

        logger.info("Start matching CategoryInfos for bookie={}", bookieId);
        logger.info("CategoryInfo count={} Category count={}", categoryInfosForBookie.size(), categories.size());

        //Here there is possibility categories and categoryInfo quantities will be different.
        for (Category category : categories) {

            CategoryInfo categoryInfo = categoryInfosForBookie.stream()
                    .filter(categoryInfoItem -> categoryInfoItem.getName().equals(category.getName()))
                    .collect(StreamUtils.singletonCollector());

            if (categoryInfo != null) {
                Pair pair = new Pair(category, categoryInfo);
                pairs.add(pair);
            } else {
                logger.warn("Could not find CategoryInfo for Category={} and Category.id={} ", category.getName(), category.getId());
            }
        }

        if (categories.size() != pairs.size()) {
            logger.warn("Not all Categories got their CategoryInfos. Category cout={}, CategoryInfo count={}",
                    categories.size(), pairs.size());
        }

        return pairs;
    }

    public List<Arb> analyze(LiveData liveData,
                             List<ge.shitbot.persist.models.Chain> rawChains, Map<Long, String> bookieNames)throws AnalyzeException {
        Analyzer analyzer = new Analyzer();
        List<Chain> chains = ChainUtils.adaptChains(rawChains);
        List<ComparableChain> comparableChains = createComparableChains(liveData, chains, bookieNames);

        //FIXME: This is only for single bookie so probably it will never generate Arbs
        return analyzer.findArbs(comparableChains);
    }
}
