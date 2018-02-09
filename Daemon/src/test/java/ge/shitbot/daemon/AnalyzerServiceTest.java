package ge.shitbot.daemon;

import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.daemon.analyze.AnalyzerService;
import ge.shitbot.daemon.analyze.models.LiveData;
import ge.shitbot.daemon.exceptions.AnalyzeException;
import ge.shitbot.daemon.util.CachedData;
import ge.shitbot.hardcode.BookieNames;
import ge.shitbot.persist.BookieRepository;
import ge.shitbot.persist.CategoryInfoRepository;
import ge.shitbot.persist.ChainRepository;
import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.models.Bookie;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.bookies.CrystalBetScraper;
import ge.shitbot.scraper.bookies.EuropeBetScraper;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.exceptions.ScraperException;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by giga on 12/7/17.
 */
public class AnalyzerServiceTest {

    Long ID = 1L;
    private Long getNextId() {
        return ID++;
    }

    private final String savedDataFile = "freshData.tmp";

    //From serialized data
    @Test
    public void testArbSearch() throws IOException, ClassNotFoundException, PersistException, AnalyzeException {
        //FileInputStream fis = new FileInputStream();
        ObjectInputStream ois = new ObjectInputStream(getClass().getResourceAsStream(savedDataFile));
        HashMap<Long, List<? extends Category>> readObjects = (HashMap<Long, List<? extends Category>>) ois.readObject();

        ChainRepository chainRepository = new ChainRepository();

        AnalyzerService analyzerService = new AnalyzerService();
        List<Arb> arbs = analyzerService.analyze(toLiveData(readObjects), chainRepository.all(), generateBookieNames(readObjects));

        ois.close();
    }

    @Test
    public void testArbSearchRemote() throws ScraperException, PersistException, AnalyzeException, ClassNotFoundException, IOException {

        BookieRepository bookieRepository = new BookieRepository();

        List<Bookie> bookies = bookieRepository.all();
        //Bookie names by ID.
        Map<Long, String> bookieNames = new HashMap<>();

        for (Bookie tmpBookie : bookies) {
            bookieNames.put(tmpBookie.getId(), tmpBookie.getName());
        }

        CachedData cachedData = new CachedData();
        HashMap<Long, List<? extends Category>> data = new HashMap<>();

        for(Map.Entry<Long, String> entry : bookieNames.entrySet()) {
            if(entry.getValue().equals(BookieNames.BET_LIVE)) continue;

            List<? extends Category> categories = cachedData.getCategories(entry.getValue());
            updateCategoryInfosForBookie(entry.getKey(), categories);
            data.put(entry.getKey(), cachedData.getCategories(entry.getValue()));
        }

        //data.put(10L, cachedData.getCategories(BookieNames.CRYSTAL_BET));
        //data.put(11L, cachedData.getCategories(BookieNames.EUROPE_BET));

        AnalyzerService analyzerService = new AnalyzerService();
        ChainRepository chainRepository = new ChainRepository();

        List<Arb> arbs = analyzerService.analyze(toLiveData(data), chainRepository.all(), bookieNames);

        System.out.println(arbs.size());
    }

    private Map<Long, String> generateBookieNames (HashMap<Long, List<? extends Category>> data) {

        Map<Long, String> result = new HashMap<>();

        for (Long bookieId : data.keySet()) {
            result.put(bookieId, "Bookie " + bookieId);
        }

        return result;
    }

    private LiveData toLiveData(HashMap<Long, List<? extends Category>> data) {

        LiveData liveData = new LiveData();

        for (Map.Entry<Long, List<? extends Category>> entry : data.entrySet()) {

            liveData.put(entry.getKey(), entry.getValue());
        }

        return liveData;
    }

    private void updateCategoryInfosForBookie(Long bookieId, List<? extends Category> data) throws PersistException {
        List<CategoryInfo> categoryInfos = new ArrayList<>();

        data.stream().forEach(category -> {

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

        CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepository();
        categoryInfoRepository.updateCategoryInfosForBookie(bookieId, categoryInfos);
    }
}
