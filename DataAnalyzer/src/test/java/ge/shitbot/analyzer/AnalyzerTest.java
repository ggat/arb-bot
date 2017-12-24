package ge.shitbot.analyzer;

import ge.shitbot.analyzer.datatypes.CategoryData;
import ge.shitbot.analyzer.datatypes.ComparableChain;
import ge.shitbot.analyzer.datatypes.EventData;
import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.core.datatypes.OddType;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by giga on 12/24/17.
 */
public class AnalyzerTest {

    @Test
    public void testFindArbs() {

        //Create ComparableChains
        List<ComparableChain> comparableChains = new ArrayList<>();

        ComparableChain comparableChainOne = new ComparableChain();
        //List<CategoryData> arbedCategoryDatas = createArbedCategoryData("Category", "SubCategory", "Real", "Barca", OddType._YES);
        List<CategoryData> arbedCategoryDatas = createMatchableCategoryDatas("Category", "SubCategory", "Real", "Barca", OddType._YES, 3.0, 3.0, 3.0);
        arbedCategoryDatas.addAll(createMatchableCategoryDatas("Category", "SubCategory", "Manchester", "Chelsea", OddType._1, 1.5, 2.1, 2.5));

        for (CategoryData categoryData : arbedCategoryDatas) {
            comparableChainOne.add(categoryData);
        }

        comparableChains.add(comparableChainOne);

        Analyzer analyzer = new Analyzer();
        List<? extends Arb> arbs = analyzer.findArbs(comparableChains);

        assertEquals(2, arbs.size());
    }

    private List<CategoryData> createArbedCategoryData(String category, String subCategory, String sideOne, String sideTwo, OddType oddType) {

        return createMatchableCategoryDatas(category, subCategory, sideOne, sideTwo, oddType, 3.0, 3.0, 3.0);
    }

    private List<CategoryData> createMatchableCategoryDatas(String category, String subCategory, String sideOne, String sideTwo, OddType oddType,
                                                            Double oddOne, Double oddTwo, Double oddThree) {

        List<CategoryData> categoryDatas = new ArrayList<>();

        // This date will be used for both events to make them arbable
        Date date = new Date();

        CategoryData data = createCategoryData("AdjaraBet", category, subCategory);
        EventData eventData = createEventData(sideOne, sideTwo, date);
        eventData.getOdds().put(oddType, oddOne);
        data.getEvents().add(eventData);

        categoryDatas.add(data);

        data = createCategoryData("EuropeBet", category, subCategory);
        eventData = createEventData(sideOne, sideTwo, date);
        eventData.getOdds().put(oddType.contrary(), oddTwo);
        data.getEvents().add(eventData);

        categoryDatas.add(data);

        data = createCategoryData("CrocoBet", category, subCategory);
        eventData = createEventData(sideOne, sideTwo, date);
        eventData.getOdds().put(oddType.contrary(), oddThree);
        data.getEvents().add(eventData);

        categoryDatas.add(data);

        return categoryDatas;
    }

    private CategoryData createCategoryData(String bookieName, String category, String subCategory) {
        CategoryData data = new CategoryData();
        data.setBookieName(bookieName);
        data.setCategory(category);
        data.setSubCategory(subCategory);

        return data;
    }

    private EventData createEventData(String sideOne, String sideTwo, Date date) {
        EventData eventData = new EventData();
        eventData.setSideOne(sideOne);
        eventData.setSideTwo(sideTwo);
        eventData.setDate(date);

        return eventData;
    }

}
