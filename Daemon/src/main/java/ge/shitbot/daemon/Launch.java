package ge.shitbot.daemon;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ge.shitbot.analyzer.Analyzer;
import ge.shitbot.analyzer.datatypes.ComparableChain;
import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.daemon.analyze.AnalyzerService;
import ge.shitbot.daemon.analyze.models.Chain;
import ge.shitbot.daemon.fetch.Collector;
import ge.shitbot.persist.BookieRepository;
import ge.shitbot.persist.CategoryInfoRepository;
import ge.shitbot.persist.ChainRepository;
import ge.shitbot.persist.exceptions.PersistException;
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

    public static void main(String[] args) {
        //
        logger.info("Daemon started.");
        logger.info("Starting data fetcher.");

        try {
            CategoryInfoRepository repository = new CategoryInfoRepository();
            BookieRepository bookieRepository = new BookieRepository();
            ChainRepository chainRepository = new ChainRepository();

            Collector.start();
            Collector.setScrapingInterval(40);

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
                Long bookieId = bookieRepository.bookieIdByName(targetBookie);

                logger.debug("BookieId for {} is {}", event.getTarget(), bookieId);

                List<? extends Category> data = event.getData();

                AnalyzerService analyzerService = new AnalyzerService();
                Analyzer analyzer = new Analyzer();
                List<Chain> chains = adaptChains(chainRepository.all());
                List<ComparableChain> comparableChains = analyzerService.createComparableChains(targetBookie, data, chains);
                List<Arb> arbs = analyzer.findArbs(comparableChains);

                logger.info("Found {} Arbs", arbs.size());
                //TODO: We have Arbs here what we do next? Send alerts, update web service.
            });

            Collector.stop();

        } catch (PersistException e) {
            logger.error("Error while trying to instantiate repository.", e.getMessage());
        }
    }

    protected static List<Chain> adaptChains (List<ge.shitbot.persist.models.Chain> chains) {

        ObjectMapper objectMapper = new ObjectMapper();
        List<Chain> adaptedChains = new ArrayList<>();

        for (ge.shitbot.persist.models.Chain chain : chains) {
            String data = chain.getData();

            JsonNode chainNode = null;
            try {
                chainNode = objectMapper.readTree(data);
            } catch (IOException e) {
                logger.warn("Could not parse chain with id {}", chain.getId());
                continue;
            }

            Chain resultChain = new Chain();
            Iterator<Map.Entry<String, JsonNode>> fields = chainNode.fields();

            while(fields.hasNext()) {
                Map.Entry<String, JsonNode> nodeEntry = fields.next();

                String bookieIdString = nodeEntry.getKey();

                if(!bookieIdString.equals("subs") && !bookieIdString.equals("edit")) {
                    Long categoryId = nodeEntry.getValue().asLong();
                    Long bookieId = Long.valueOf(bookieIdString);

                    resultChain.put(bookieId, categoryId);
                }
            }

            adaptedChains.add(resultChain);
        }

        return adaptedChains;
    }
}
