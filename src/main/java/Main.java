import drivers.bookies.*;
import drivers.BookieDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.math.BigDecimal;

/**
 * Created by giga on 9/13/17.
 */
public class Main {

    public static void main(String[] args) throws Throwable {

        BookieDriver europe = new EuropeBetDriver(new ChromeDriver());
        BookieDriver adjara = new AdjaraBetDriver(new ChromeDriver());
        //BookieDriver betLive = new BetLiveDriver(new ChromeDriver());
        //BookieDriver liderBetDriver = new LiderBetDriver(new ChromeDriver());
        //BookieDriver crystalBetDriver = new CrystalBetDriver(new ChromeDriver());

        Long adjaraBalance = adjara.getBalance();
        Long europeBalance = europe.getBalance();

        System.out.printf("adjaraBalance, %d", adjaraBalance);
        System.out.printf("europeBalance, %d", europeBalance);
        System.out.printf("Total blance, %d", (adjaraBalance + europeBalance) / 100);

        //ArrayList<String> tabs = new ArrayList<String>(driver.getWindowHandles());

        //bookieDriver.maximize();

    }
}
