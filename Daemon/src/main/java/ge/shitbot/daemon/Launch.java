package ge.shitbot.daemon;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ge.shitbot.analyzer.Analyzer;
import ge.shitbot.analyzer.datatypes.ComparableChain;
import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.daemon.analyze.AnalyzerService;
import ge.shitbot.daemon.analyze.models.Chain;
import ge.shitbot.daemon.analyze.models.LiveData;
import ge.shitbot.daemon.exceptions.AnalyzeException;
import ge.shitbot.daemon.fetch.Collector;
import ge.shitbot.hardcode.BookieNames;
import ge.shitbot.persist.ArbInfoRepository;
import ge.shitbot.persist.BookieRepository;
import ge.shitbot.persist.CategoryInfoRepository;
import ge.shitbot.persist.ChainRepository;
import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.models.ArbInfo;
import ge.shitbot.persist.models.Bookie;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.datatypes.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * Created by giga on 12/2/17.
 */
public class Launch {

    private static Logger logger = LoggerFactory.getLogger(Launch.class);
    private static LiveData liveData = new LiveData();

    public static void main(String[] args) {
        //
        logger.info("Daemon started.");
        logger.info("Starting data fetcher.");

        try {
            CategoryInfoRepository repository = new CategoryInfoRepository();
            BookieRepository bookieRepository = new BookieRepository();
            ChainRepository chainRepository = new ChainRepository();
            ArbInfoRepository arbInfoRepository = new ArbInfoRepository();

            Collector.start();
            Collector.setScrapingInterval(40);

            // Update LiveData handler
            Collector.addUpdateEventHandler(event -> {

                //logger.info("Handling update event for {} with {} event(s)", event.getTarget(), event.getData().size());

                String targetBookie = event.getTarget();
                Long bookieId = bookieRepository.bookieIdByName(targetBookie);

                liveData.put(bookieId, event.getData());
            });

            // Update CategoryInfos handler
            Collector.addUpdateEventHandler(event -> {

                logger.info("Handling update event for {} with {} event(s)", event.getTarget(), event.getData().size());

                String targetBookie = event.getTarget();
                Long bookieId = bookieRepository.bookieIdByName(targetBookie);

                logger.debug("BookieId for {} is {}", event.getTarget(), bookieId);

                List<? extends Category> data = event.getData();
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

                logger.info("Saving CategoryInfos for {}", event.getTarget(), bookieId);
                repository.updateCategoryInfosForBookie(bookieId, categoryInfos);
                logger.info("Data size: {}", data.size());
            });

            // Search for Arbs handler
            Collector.addUpdateEventHandler(event -> {

                logger.info("Start searching for new Arbs. {} data({}) was just updated", event.getTarget(), event.getData().size());

                String targetBookie = event.getTarget();
                Bookie bookie = bookieRepository.byName(targetBookie);
                Long bookieId = bookie.getId();

                logger.debug("BookieId for {} is {}", event.getTarget(), bookieId);

                List<? extends Category> data = event.getData();

                AnalyzerService analyzerService = new AnalyzerService();
                List<Bookie> bookies = bookieRepository.all();
                //Bookie names by ID.
                Map<Long, String> bookieNames = new HashMap<>();

                for (Bookie tmpBookie : bookies) {
                    bookieNames.put(tmpBookie.getId(), tmpBookie.getName());
                }

                try {
                    List<Arb> arbs = analyzerService.analyze(liveData, chainRepository.all(), bookieNames);
                    logger.info("Found {} Arbs", arbs.size());

                    try {

                        ObjectMapper mapper = new ObjectMapper();
                        String arbInfoData = mapper.writeValueAsString(arbs);
                        ArbInfo arbInfo = new ArbInfo();
                        arbInfo.setData(arbInfoData);
                        arbInfoRepository.truncate();
                        arbInfoRepository.saveArbInfo(arbInfo);
                    } catch (JsonProcessingException e) {

                        logger.error("Could not create JSON of ArbInfo list. {}", e);
                    }
                } catch (AnalyzeException e) {

                    logger.error("Error while analyzing data. {}", e);
                }
            });

            Collector.stop();

        } catch (PersistException e) {
            logger.error("Error while trying to instantiate repository.", e.getMessage());
        }
    }
}
