package ge.arb-bot.daemon;

import ge.arb-bot.core.datatypes.Arb;
import ge.arb-bot.daemon.analyze.AnalyzerService;
import ge.arb-bot.daemon.analyze.models.LiveData;
import ge.arb-bot.daemon.analyze.utils.categories.CategoryCategoryInfoPair;
import ge.arb-bot.daemon.exceptions.AnalyzeException;
import ge.arb-bot.daemon.util.CachedData;
import ge.arb-bot.daemon.util.Generator;
import ge.arb-bot.hardcode.BookieNames;
import ge.arb-bot.persist.BookieRepository;
import ge.arb-bot.persist.CategoryInfoRepository;
import ge.arb-bot.persist.ChainRepository;
import ge.arb-bot.persist.exceptions.PersistException;
import ge.arb-bot.persist.models.Bookie;
import ge.arb-bot.persist.models.CategoryInfo;
import ge.arb-bot.scraper.bookies.CrystalBetScraper;
import ge.arb-bot.scraper.bookies.EuropeBetScraper;
import ge.arb-bot.scraper.datatypes.Category;
import ge.arb-bot.scraper.exceptions.ScraperException;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

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

    @Test
    public void testCachedCategoryCategoryInfo() throws PersistException {

        AnalyzerService analyzerService = new AnalyzerService();

        //CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepository();
        //List<? extends CategoryInfo> categoryInfosForBookie = categoryInfoRepository.getCategoryInfosForBookie(bookieId);

        Long[] bookieIds = {1L, 2L, 3L};
        Map<Long, List<Category>> categoriesByBookie = new HashMap<>();
        Map<Long, List<? extends CategoryInfo>> categoryInfosByBookie = new HashMap<>();

        for(Long bookieId : bookieIds) {
            List<Category> categories = new ArrayList<>();
            List<CategoryInfo> categoryInfos = new ArrayList<>();
            for (int i = 0; i < 71; i++) {

                Category category = Generator.randomCategory();
                categories.add(category);

                CategoryInfo categoryInfo = new CategoryInfo();
                categoryInfo.setId(new Long(Generator.randomId()));
                categoryInfo.setName(category.getName());
                categoryInfos.add(categoryInfo);
            }

            categoriesByBookie.put(bookieId, categories);
            categoryInfosByBookie.put(bookieId, categoryInfos);
        }

        for(int l=0; l < 4; l++) {
            for (Long bookieId : bookieIds) {
                List<Category> categories = categoriesByBookie.get(bookieId);
                List<? extends CategoryInfo> categoryInfos = categoryInfosByBookie.get(bookieId);
                List<CategoryCategoryInfoPair> pairs = analyzerService.cachedCategoryCategoryInfos(bookieId, categories, categoryInfos);

                assertNotNull(pairs);
            }
        }

        //System.out.println(pairs);
    }
}
