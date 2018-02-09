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
import ge.shitbot.daemon.exceptions.BookieNotFoundException;
import ge.shitbot.daemon.exceptions.BookieScraperNotFoundException;
import ge.shitbot.daemon.fetch.BookieScraperRegistry;
import ge.shitbot.daemon.fetch.Collector;
import ge.shitbot.hardcode.BookieNames;
import ge.shitbot.persist.*;
import ge.shitbot.persist.config.PersistConfig;
import ge.shitbot.persist.config.PersistConfigBuilder;
import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.models.ArbInfo;
import ge.shitbot.persist.models.Bookie;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.BookieScraper;
import ge.shitbot.scraper.datatypes.Category;
import ge.shitbot.scraper.exceptions.ScraperException;
import org.apache.commons.configuration2.HierarchicalConfiguration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.commons.configuration2.tree.ImmutableNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by giga on 12/2/17.
 */
public class Launch {

    private static Logger logger = LoggerFactory.getLogger(Launch.class);
    private static LiveData liveData = new LiveData();
    private static List<Bookie> bookies;

    public static void main(String[] args) throws ConfigurationException {

        try {
            bootstrap();
            run();
        } catch (ConfigurationException e) {
            logger.error("Could not bootstrap application because configuration problem: {}", e);
        } catch (Exception e) {
            logger.error("Unexpected error exception thrown {}", e);
        }
    }

    // Temporary disabled multithread operation
    /*public static void run() {
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
            logger.error("Error while trying to instantiate repository. {}", e);
        }
    }*/

    private static void run() throws PersistException {

        CategoryInfoRepository categoryInfoRepository = new CategoryInfoRepository();
        ChainRepository chainRepository = new ChainRepository();
        ArbInfoRepository arbInfoRepository = new ArbInfoRepository();
        Map<Long, BookieScraper> scrapers = new HashMap<>();

        // Create scrapers for each Bookie.
        for (String bookieName : BookieNames.asList()) {
            try {
                scrapers.put(bookieId(bookieName), BookieScraperRegistry.getScraper(bookieName));
            } catch (BookieScraperNotFoundException e) {
                logger.error("Scraper not found for {}", bookieName);
                logger.error("Skipping this bookie...");
            } catch (BookieNotFoundException e) {
                logger.error("Bookie not found while trying to get its id by name. {}", e);
                logger.error("Skipping this bookie...");
            }
        }

        while(true) {
            // Update category infos into DB
            for (Map.Entry<Long, BookieScraper> entry : scrapers.entrySet()) {
                Long bookieId = entry.getKey();
                BookieScraper scraper = entry.getValue();

                List<? extends Category> categories = null;
                try {
                    categories = scraper.getFreshData();
                } catch (ScraperException e) {
                    logger.warn("Data scraping failed for bookie={} skipping...", bookieId);
                    continue;
                }

                // Update liveData
                liveData.put(bookieId, categories);

                List<CategoryInfo> categoryInfos = new ArrayList<>();

                categories.stream().forEach(category -> {

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

                logger.info("Saving CategoryInfos for {}", bookieId);
                categoryInfoRepository.updateCategoryInfosForBookie(bookieId, categoryInfos);

                // Search for arbs
                AnalyzerService analyzerService = new AnalyzerService();
                try {
                    Map<Long, String> bookieNames = new HashMap<>();

                    for (Bookie tmpBookie : bookies) {
                        bookieNames.put(tmpBookie.getId(), tmpBookie.getName());
                    }

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
            }
        }

    }

    private static void bootstrap() throws ConfigurationException, PersistException {

        Configurations configs = new Configurations();
        XMLConfiguration config = configs.xml(new File(System.getProperty("user.dir")) + "/env.xml");

        //Bootstrap the persistence
        PersistConfig persistConfig = new PersistConfigBuilder()
                .url(config.getString("db.url"))
                .driver(config.getString("db.driver"))
                .user(config.getString("db.user"))
                .pass(config.getString("db.pass"))
                .dialect(config.getString("db.dialect"))
                .build();

        PersistFacade.setSettings(persistConfig);
        //END Persist config

        BookieRepository bookieRepository = new BookieRepository();
        bookies = bookieRepository.all();

        System.out.println(persistConfig);
    }

    private static Long bookieId(String bookieName) throws BookieNotFoundException {
        for (Bookie bookie : bookies) {
            if (bookie.getName().equals(bookieName)) return bookie.getId();
        }

        throw new BookieNotFoundException("Bookie with id="+ bookieName +" not found");
    }

    private static String getBookieName(Long bookieId) throws BookieNotFoundException {
        for (Bookie bookie : bookies) {
            if (bookie.getId().equals(bookieId)) return bookie.getName();
        }

        throw new BookieNotFoundException("Bookie with id="+ bookieId +" not found");
    }
}
