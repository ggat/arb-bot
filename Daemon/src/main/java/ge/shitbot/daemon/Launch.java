package ge.shitbot.daemon;

import ge.shitbot.daemon.fetch.Collector;
import ge.shitbot.hardcode.BookieNames;
import ge.shitbot.scraper.datatypes.Category;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Created by giga on 12/2/17.
 */
public class Launch {

    private static Logger logger = LoggerFactory.getLogger(Launch.class);

    public static void main(String[] args) {
        //
        logger.info("Daemon started.");
        logger.info("Starting data fetcher.");

        Collector.start();
        Collector.setScrapingInterval(40);
        Collector.addUpdateEventHandler(event -> {

            List<? extends Category> data = event.getData();

            logger.info("Data size: {}", data.size());
        });

        Collector.stop();
    }
}
