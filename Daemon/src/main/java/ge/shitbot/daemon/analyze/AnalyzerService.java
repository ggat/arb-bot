package ge.shitbot.daemon.analyze;

import ge.shitbot.analyzer.Analyzer;
import ge.shitbot.analyzer.datatypes.CategoryData;
import ge.shitbot.analyzer.datatypes.ComparableChain;
import ge.shitbot.analyzer.datatypes.EventData;
import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.core.datatypes.util.StreamUtils;
import ge.shitbot.daemon.analyze.models.Chain;
import ge.shitbot.daemon.analyze.models.LiveData;
import ge.shitbot.daemon.analyze.utils.ChainUtils;
import ge.shitbot.daemon.analyze.utils.categories.CategoryCategoryInfoMapper;
import ge.shitbot.daemon.analyze.utils.categories.CategoryCategoryInfoPair;
import ge.shitbot.daemon.exceptions.AnalyzeException;
import ge.shitbot.persist.CategoryInfoRepository;
import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
                             List<ge.shitbot.persist.models.Chain> rawChains, Map<Long, String> bookieNames) throws AnalyzeException {
        Analyzer analyzer = new Analyzer();

        List<Chain> chains = ChainUtils.adaptChains(rawChains);
        List<ComparableChain> comparableChains = createComparableChains(liveData, chains, bookieNames);

        logger.info("Created {} ", comparableChains.size());

        return analyzer.findArbs(comparableChains, -7.0);
    }
}
