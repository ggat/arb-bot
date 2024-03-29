package ge.arb-bot.daemon.analyze;

import ge.arb-bot.analyzer.Analyzer;
import ge.arb-bot.analyzer.SimpleAnalyzer;
import ge.arb-bot.analyzer.datatypes.CategoryData;
import ge.arb-bot.analyzer.datatypes.ComparableChain;
import ge.arb-bot.analyzer.datatypes.EventData;
import ge.arb-bot.core.datatypes.Arb;
import ge.arb-bot.core.datatypes.util.StreamUtils;
import ge.arb-bot.daemon.analyze.models.Chain;
import ge.arb-bot.daemon.analyze.models.LiveData;
import ge.arb-bot.daemon.analyze.utils.ChainUtils;
import ge.arb-bot.daemon.analyze.utils.categories.CategoryCategoryInfoMapper;
import ge.arb-bot.daemon.analyze.utils.categories.CategoryCategoryInfoPair;
import ge.arb-bot.daemon.exceptions.AnalyzeException;
import ge.arb-bot.persist.CategoryInfoRepository;
import ge.arb-bot.persist.exceptions.PersistException;
import ge.arb-bot.persist.models.CategoryInfo;
import ge.arb-bot.scraper.datatypes.Category;
import ge.arb-bot.scraper.datatypes.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by giga on 12/21/17.
 */
public class AnalyzerService {

    Logger logger = LoggerFactory.getLogger(AnalyzerService.class);

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

                    CategoryCategoryInfoMapper mapper = new CategoryCategoryInfoMapper();
                    CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepository();
                    List<? extends CategoryInfo> categoryInfosForBookie = categoryInfoRepository.getCategoryInfosForBookie(bookieId);
                    List<CategoryCategoryInfoPair> pairs = mapper.map(categories, categoryInfosForBookie, bookieId);

                    // Check if value of this chainItem matches any Category of this bookie.
                    // Chain can include values for any bookie. So here chainItem may not be for targetBookie
                    // If chainItem is for targetBookie, it must find referenced category, or chain is corrupt.

                    CategoryCategoryInfoPair found = pairs.stream()
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

                if(comparableChain.size() == 0) {
                    continue;
                }

                comparableChains.add(comparableChain);
            }

        } catch (PersistException e) {
            throw new AnalyzeException(e);
        }

        return comparableChains;
    }

    /**
     * Category/CategoryInfo mapping for bookies.
     */
    Map<Long, List<CategoryCategoryInfoPair>> categoryCategoryInfoMap = new HashMap<>();

    /**
     * Creates, saves, and returns Category/CategoryInfo mapping for this bookie, if does not exist yet.
     * If mapping already exists it will be directly returned.
     *
     * @param bookieId
     * @param categories
     * @return
     * @throws PersistException
     */
    public List<CategoryCategoryInfoPair> cachedCategoryCategoryInfos(Long bookieId, List<? extends Category> categories, List<? extends CategoryInfo> categoryInfosForBookie) throws PersistException {

        List<CategoryCategoryInfoPair> pairs = categoryCategoryInfoMap.get(bookieId);

        if(pairs == null) {
            CategoryCategoryInfoMapper mapper = new CategoryCategoryInfoMapper();
            pairs = mapper.map(categories, categoryInfosForBookie, bookieId);
            categoryCategoryInfoMap.put(bookieId, pairs);
        }

        return pairs;
    }

    public List<Arb> analyze(LiveData liveData,
                             List<ge.arb-bot.persist.models.Chain> rawChains, Map<Long, String> bookieNames) throws AnalyzeException {
        Analyzer analyzer = new Analyzer();

        List<Chain> chains = ChainUtils.adaptChains(rawChains);
        List<ComparableChain> comparableChains = createComparableChains(liveData, chains, bookieNames);

        logger.info("Created {} ", comparableChains.size());

        return analyzer.findArbs(comparableChains, -7.0);
    }

    public List<Arb> analyze(LiveData liveData, Map<Long, String> bookieNames) throws AnalyzeException {

        try {
            SimpleAnalyzer analyzer = SimpleAnalyzer.getInstance();
            List<? extends CategoryData> categoryDatas = liveDataToCategoryDatas(liveData, bookieNames);

            return analyzer.findArbs(categoryDatas);

        } catch (Exception e) {
            throw new AnalyzeException(e);
        }
    }

    protected List<? extends CategoryData> liveDataToCategoryDatas(LiveData liveData, Map<Long, String> bookieNames) {

        List<CategoryData> categoryDatas = new ArrayList<>();

        for (Map.Entry<Long, List<? extends Category>> categoriesForBookie : liveData.entrySet()) {

            for (Category category : categoriesForBookie.getValue()) {
                for (Category subCategory : category.getSubCategories()) {
                    if (subCategory.getEvents().size() > 0) {
                        categoryDatas.add(createCategoryData(subCategory, categoriesForBookie.getKey(), bookieNames));
                    }
                }
            }
        }

        return categoryDatas;
    }

    protected CategoryData createCategoryData(Category category, Long bookieId, Map<Long, String> bookieNames) {
        CategoryData categoryData = new CategoryData();
        categoryData.setBookieName(bookieNames.get(bookieId));
        categoryData.setCategory(category.getParent().getName());
        categoryData.setSubCategory(category.getName());

        for (Event event : category.getEvents()) {
            EventData eventData = new EventData();
            eventData.setDate(event.getDate());
            eventData.setOdds(event.getOdds());
            eventData.setSideOne(event.getSideOne());
            eventData.setSideTwo(event.getSideTwo());

            categoryData.getEvents().add(eventData);
        }

        return categoryData;
    }
}
