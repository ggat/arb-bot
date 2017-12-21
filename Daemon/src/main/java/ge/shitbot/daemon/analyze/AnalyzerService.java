package ge.shitbot.daemon.analyze;

import ge.shitbot.analyzer.datatypes.CategoryData;
import ge.shitbot.analyzer.datatypes.ComparableChain;
import ge.shitbot.analyzer.datatypes.EventData;
import ge.shitbot.core.datatypes.util.StreamUtils;
import ge.shitbot.daemon.analyze.models.Chain;
import ge.shitbot.daemon.analyze.models.FlatCategories;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.datatypes.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by giga on 12/21/17.
 */
public class AnalyzerService {

    public List<ComparableChain> createComparableChains(String targetBookie, List<? extends Category> categories, List<Chain> chains) {
        List<ComparableChain> comparableChains = new ArrayList<>();

        FlatCategories flatCategories = new FlatCategories(categories);

        for(Chain chain : chains) {

            ComparableChain comparableChain = new ComparableChain();

            for(Map.Entry<Long, Long> chainItem : chain.entrySet()) {
                Category found = flatCategories.stream().filter(category -> {

                    return category.getId().equals(chainItem.getValue());

                }).collect(StreamUtils.singletonCollector());

                if(found != null) {

                    //TODO: Look at me.
                    //FIXME: This will not work if there is subCategory of subCatgory
                    //FIXME: It would be much better solution if we checked if subCategory have events directly
                    //Here we check if found category and if category is subCategory

                    //if(found.getParent() != null) {
                    if(found.getEvents().size() > 0) {
                        CategoryData categoryData = new CategoryData();
                        categoryData.setBookieName(targetBookie);
                        categoryData.setCategory(found.getParent().getName());
                        categoryData.setSubCategory(found.getName());

                        for(Event event : found.getEvents()) {
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

        return comparableChains;
    }
}
