package ge.shitbot.daemon;

import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.core.datatypes.util.FileSerializer;
import ge.shitbot.daemon.analyze.AnalyzerService;
import ge.shitbot.daemon.analyze.models.LiveData;
import ge.shitbot.daemon.exceptions.AnalyzeException;
import ge.shitbot.persist.ChainRepository;
import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.scraper.bookies.CrystalBetScraper;
import ge.shitbot.scraper.bookies.EuropeBetScraper;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.exceptions.ScraperException;
import org.junit.Test;

import java.io.IOException;
import java.io.ObjectInputStream;
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

        //List<? extends Category> crystalCategories = (new CrystalBetScraper()).getFreshData();
        //List<? extends Category> europeCategories = (new EuropeBetScraper()).getFreshData();

        List<? extends Category> crystalCategories = (List<? extends Category>) FileSerializer.fromFile("crystalCategories.dump");
        List<? extends Category> europeCategories = (List<? extends Category>) FileSerializer.fromFile("europeCategories.dump");


        //System.out.println(europeCategories);
        HashMap<Long, List<? extends Category>> data = new HashMap<>();
        data.put(10L, crystalCategories);
        data.put(11L, europeCategories);

        AnalyzerService analyzerService = new AnalyzerService();
        ChainRepository chainRepository = new ChainRepository();

        List<Arb> arbs = analyzerService.analyze(toLiveData(data), chainRepository.all(), generateBookieNames(data));

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
}
