package ge.shitbot.bot.drivers;

import ge.shitbot.bot.drivers.bookies.*;
import ge.shitbot.bot.exceptions.BookieDriverNotFoundException;
import org.openqa.selenium.chrome.ChromeDriver;

/**
 * Created by giga on 10/11/17.
 */
public class BookieDriverRegistry {

    //FIXME: ChromeDriver should not be forced here.
    public static BookieDriver getDriver(String name) throws BookieDriverNotFoundException {

        BookieDriver created;

        switch (name) {
            case "AdjaraBet":
                created = new AdjaraBetDriver(new ChromeDriver());
            break;
            case "BetLive":
                created = new BetLiveDriver(new ChromeDriver());
            break;
            case "CrystalBet":
                created = new CrystalBetDriver(new ChromeDriver());
            break;
            case "EuropeBet":
                created = new EuropeBetDriver(new ChromeDriver());
            break;
            case "LiderBet":
                created = new LiderBetDriver(new ChromeDriver());
            break;
            default:
                throw new BookieDriverNotFoundException("Driver not found for bookie with name " + name);
        }

        return created;
    }

    public static void main(String[] args) {

        try {
            BookieDriver driver = getDriver("BetLive");
        } catch (BookieDriverNotFoundException e) {
            e.printStackTrace();
        }

        //System.out.println(ge.shitbot.bot.drivers.get("BetLive"));
    }
}
