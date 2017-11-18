import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by giga on 9/13/17.
 */
public class Main {

    static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Throwable {

        DriverTests driverTests = new DriverTests();

        logger.info("Before run tests");

        driverTests.testAllBookies();

        logger.info("After run tests");

    }
}
