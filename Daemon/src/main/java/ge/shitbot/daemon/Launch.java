package ge.shitbot.daemon;

import ge.shitbot.daemon.fetch.Collector;
import ge.shitbot.hardcode.BookieNames;
import ge.shitbot.persist.BookieRepository;
import ge.shitbot.persist.CategoryInfoRepository;
import ge.shitbot.persist.exceptions.PersistException;
import ge.shitbot.persist.models.CategoryInfo;
import ge.shitbot.scraper.datatypes.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

            Collector.start();
            Collector.setScrapingInterval(40);
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

            Collector.stop();

        } catch (PersistException e) {
            logger.error("Error while trying to instantiate repository.", e.getMessage());
        }
    }
}
