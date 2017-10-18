import ge.shitbot.bot.drivers.BookieDriver;
import ge.shitbot.bot.drivers.bookies.CrocoBetDriver;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by giga on 9/13/17.
 */
public class Main {

    public static void main(String[] args) throws Throwable {

        /*Long totalBalance = 0L;

        List<BookieDriver> bookieDrivers = new ArrayList<BookieDriver>();

        //bookieDrivers.add(new EuropeBetDriver(new ChromeDriver()));
        bookieDrivers.add(new AdjaraBetDriver(new ChromeDriver()));
        bookieDrivers.add(new BetLiveDriver(new ChromeDriver()));
        bookieDrivers.add(new LiderBetDriver(new ChromeDriver()));
        bookieDrivers.add(new CrystalBetDriver(new ChromeDriver()));

        for(BookieDriver driver : bookieDrivers) {

            try {

                Long balance = driver.getBalance();

                System.out.println(driver.getClass().getName() + ": " + balance);

                totalBalance += balance;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                break;
            }
        }

        System.out.println("End balance: " + totalBalance);*/

        //BookieDriver bookieDriver = new BetLiveDriver(new ChromeDriver());
        BookieDriver bookieDriver = new CrocoBetDriver(new ChromeDriver());

        bookieDriver.createBet("ესპანეთი", "ლა ლიგა", "ბეტისი", "ალავესი", "Yes", 24.0, 1.65);
        //Long balance = bookieDriver.getBalance();

        //System.out.printf("Balance: " + balance);

        /*BookieDriver europe = new EuropeBetDriver(new ChromeDriver());
        BookieDriver adjara = new AdjaraBetDriver(new ChromeDriver());
        BookieDriver betLive = new BetLiveDriver(new ChromeDriver());
        BookieDriver liderBetDriver = new LiderBetDriver(new ChromeDriver());
        BookieDriver crystalBetDriver = new CrystalBetDriver(new ChromeDriver());

        Long adjaraBalance = adjara.getBalance();
        Long europeBalance = europe.getBalance();
        Long betLiveBalance = betLive.getBalance();
        Long liderBetDriverBalance = liderBetDriver.getBalance();
        Long crystalBetDriverBalance = crystalBetDriver.getBalance();

        //System.out.printf("adjaraBalance, %d", adjaraBalance);
        //System.out.printf("europeBalance, %d", europeBalance);
        //System.out.printf("betLiveBalance, %d", betLiveBalance);
        //System.out.printf("liderBetDriverBalance, %d", liderBetDriverBalance);
        //System.out.printf("crystalBetDriver, %d", crystalBetDriverBalance);
        System.out.printf("Total blance, %d", (adjaraBalance + europeBalance + betLiveBalance + liderBetDriverBalance + crystalBetDriverBalance) / 100);*/

        //ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());

        //bookieDriver.maximize();

    }
}
