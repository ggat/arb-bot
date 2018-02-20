package ge.shitbot.analyzer;

import ge.shitbot.analyzer.datatypes.CategoryData;
import ge.shitbot.analyzer.datatypes.ComparableChain;
import ge.shitbot.analyzer.datatypes.EventData;
import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.core.datatypes.OddType;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by giga on 12/24/17.
 */
public class SimpleAnalyzerTest {

    @Test
    public void testMergeChains() throws Exception {

        final String A = "A";
        final String B = "B";
        final String C = "C";

        Map<String, String> chain1 = new HashMap<>();
        Map<String, String> chain2 = new HashMap<>();

        //chain1.put(A, "A_Chain_1");
        chain1.put(B, "B_Chain_1");
        chain1.put(C, "C_Chain_1");

        chain2.put(A, "A_Chain_2ddasd");
        //chain2.put(null, "nusadsad");
        //chain1.put(B, "B_Chain_2");
        chain1.put(C, "C_Chain_2");

        SimpleAnalyzer simpleAnalyzer = SimpleAnalyzer.getInstance();
        Map<String, String> stringMap = simpleAnalyzer.mergeChains(chain1, chain2);

        Map<String, String> toThis = new HashMap<>();
        toThis.put(A, "A_Chain_2ddasd");
        toThis.put(B, "B_Chain_1");
        toThis.put(C, "C_Chain_2");

        assertEquals(toThis, stringMap);
    }

    @Test
    public void testFindArbs() throws Exception {

        //List<CategoryData> arbedCategoryDatas = createArbedCategoryData("Category", "SubCategory", "Real", "Barca", OddType._YES);
        List<CategoryData> categoryDatas = new ArrayList<>();

        Date date = new Date(2017, 12, 21, 0, 0, 0);

        categoryDatas.add(createFullCategoryData("Adjara", "Spain", "La liga", "Dinamo_1", "Manchester_1", date));
        categoryDatas.add(createFullCategoryData("Croco", "Spain", "La liga", "Dinamo_2", "Manchester_2", date));
        categoryDatas.add(createFullCategoryData("Europe", "Spain", "La liga", "Dinamo_3", "Manchester_3", date));
        categoryDatas.add(createFullCategoryData("Crystal", "Spain", "La liga", "Dinamo_4", "Manchester_4", date));
        categoryDatas.add(createFullCategoryData("Betlive", "Spain", "La liga", "Dinamo_1", "Manchester_5", date));
        categoryDatas.add(createFullCategoryData("LiderBet", "Spain", "La liga", "Dinamo_2", "Manchester_6", date));
        //categoryDatas.add(createFullCategoryData("Adjara", "Spain", "La liga", "Dinamo_1", "Manchester_1", date));

        SimpleAnalyzer simpleAnalyzer = new SimpleAnalyzer();
        simpleAnalyzer.findArbs(categoryDatas);

    }

    private CategoryData createFullCategoryData(String bookieName, String category, String subCategory, String sideOne, String sideTwo, Date date) {
        CategoryData categoryData = createCategoryData(bookieName, category, subCategory);
        EventData eventData = createEventData(sideOne, sideTwo, date);
        List<EventData> eventDatas = new ArrayList<>();
        eventDatas.add(eventData);

        categoryData.setEvents(eventDatas);

        return categoryData;
    }

    @Test
    public void testReset() throws IOException, ClassNotFoundException {
        SimpleAnalyzer analyzer = SimpleAnalyzer.getInstance();
        analyzer.reset();

        assertEquals(new TeamNameChains(), analyzer.getTeamNameChains());
    }

    @Test
    public void scenario1Test() throws IOException, ClassNotFoundException  {

        testReset();

        final String ADJ = "ADJ";
        final String CRO = "CRO";
        final String EUR = "EUR";
        final String LDB = "LDB";
        final String BET = "BET";
        final String CRY = "CRY";

        List<CategoryData> categoryDatas = new ArrayList<>();
        Date date = new Date();
        categoryDatas.add(createFullCategoryData(ADJ, "Spain", "La liga", "Liverpool_1", "Manchester_1", date));
        categoryDatas.add(createFullCategoryData(CRO, "Spain", "La liga", "Liverpool_2", "Manchester_2", date));
        categoryDatas.add(createFullCategoryData(EUR, "Spain", "La liga", "Liverpool_3", "Manchester_3", date));
        categoryDatas.add(createFullCategoryData(LDB, "Spain", "La liga", "Liverpool_4", "Manchester_4", date));
        categoryDatas.add(createFullCategoryData(BET, "Spain", "La liga", "Liverpool_5", "Manchester_1", date));
        categoryDatas.add(createFullCategoryData(CRY, "Spain", "La liga", "Liverpool_6", "Manchester_2", date));

        SimpleAnalyzer analyzer = SimpleAnalyzer.getInstance();
        analyzer.findArbs(categoryDatas);
        TeamNameChains chains = analyzer.getTeamNameChains();

        Map<String, String> chain1 = new HashMap<>();
        chain1.put(BET, "Liverpool_5");
        chain1.put(ADJ, "Liverpool_1");

        Map<String, String> chain2 = new HashMap<>();
        chain2.put(CRY, "Liverpool_6");
        chain2.put(CRO, "Liverpool_2");

        Map<String, String> chain1_1 = new HashMap<>();
        chain1_1.put(BET, "Manchester_1");
        chain1_1.put(ADJ, "Manchester_1");

        Map<String, String> chain2_1 = new HashMap<>();
        chain2_1.put(CRY, "Manchester_2");
        chain2_1.put(CRO, "Manchester_2");

        assertEquals(4, chains.size());
        assertThat(chains, hasItems(chain1, chain2, chain1_1, chain2_1));

        categoryDatas.add(createFullCategoryData(BET, "Spain", "La liga", "Liverpool_5", "Chelsea_1", date));
        categoryDatas.add(createFullCategoryData(CRY, "Spain", "La liga", "Liverpool_6", "Chelsea_1", date));

        analyzer.findArbs(categoryDatas);
        chains = analyzer.getTeamNameChains();

        Map<String, String> chain3 = new HashMap<>();
        chain3.put(CRY, "Liverpool_6");
        chain3.put(CRO, "Liverpool_2");
        chain3.put(BET, "Liverpool_5");
        chain3.put(ADJ, "Liverpool_1");

        Map<String, String> chain3_1 = new HashMap<>();
        chain3_1.put(BET, "Chelsea_1");
        chain3_1.put(CRY, "Chelsea_1");

        chain1 = chain2 = null;

        assertEquals(4, chains.size());
        assertThat(chains, hasItems(chain1_1, chain2_1, chain3, chain3_1));

        System.out.println(analyzer.getTeamNameChains());
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
