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
        Map<String, List<? extends Category>> data = Collector.getData();

        while (true) {

            for (String bookieName : BookieNames.asList()) {
                List<? extends Category> categories = data.get(bookieName);

                logger.info("Currently there are {} bookies parsed.", data.size());

                if(categories != null) {
                    logger.info("Now we can analyze data for {} again, that has {} categories.", bookieName,
                            categories.size());
                }
            }
            
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e1) {

                logger.debug("Launcher interrupted.");
                Collector.stop();
                Thread.currentThread().interrupt();
            }
        }
    }
}
