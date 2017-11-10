import ge.shitbot.bot.drivers.BookieDriver;
import ge.shitbot.bot.drivers.bookies.AdjaraBetDriver;
import ge.shitbot.bot.drivers.bookies.CrocoBetDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by giga on 9/13/17.
 */
public class Main {

    public static void main(String[] args) throws Throwable {

        DriverTests driverTests = new DriverTests();

        driverTests.testAllBookies();

    }
}
