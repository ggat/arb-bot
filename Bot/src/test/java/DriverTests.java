import ge.shitbot.datasources.source.NewDataSource;
import ge.shitbot.hardcode.BookieNames;
import ge.shitbot.bot.drivers.BookieDriver;
import ge.shitbot.bot.drivers.BookieDriverRegistry;
import ge.shitbot.bot.exceptions.BookieDriverNotFoundException;
import ge.shitbot.bot.exceptions.UnknownOddTypeException;
import ge.shitbot.core.datatypes.Arb;
import ge.shitbot.datasources.exceptions.DataSourceException;
import ge.shitbot.datasources.source.MainDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by giga on 11/10/17.
 */
public class DriverTests {

    Logger logger = LoggerFactory.getLogger(DriverTests.class);

    Map<String, BookieStatus> statuses = new HashMap<>();

    private enum BookieStatus {
        WORKS, FAILS, UNABLE_TO_CHECK
    }

    public void testAllBookies() {

        logger.info("Going to run tests for all bookies.");

        NewDataSource<Arb> arbMainDataSource = new NewDataSource<>();

        statuses.clear();

        try {
            List<Arb> arbs = arbMainDataSource.getArbs();

//            testBookieAndSaveStatus(BookieNames.AJARA_BET, arbs);
//            testBookieAndSaveStatus(BookieNames.EUROPE_BET, arbs);
            testBookieAndSaveStatus(BookieNames.LIDER_BET, arbs);
//            testBookieAndSaveStatus(BookieNames.BET_LIVE, arbs);
//            testBookieAndSaveStatus(BookieNames.CRYSTAL_BET, arbs);
//            testBookieAndSaveStatus(BookieNames.CROCO_BET, arbs);

            statuses.forEach((k, v) -> {
                System.out.println(k + ": " + v);
            });

        } catch (DataSourceException ex) {

            ex.printStackTrace();

            logger.error("Could not get data from datasource: ", ex.getMessage());
        }
    }

    protected List<Arb.Bookie> getOddsForBookie(List<Arb> arbs, String bookieName) {
        return arbs.stream()
                .filter(arb ->
                        bookieName.equals(arb.getBookieOne().getName()) ||
                                bookieName.equals(arb.getBookieTwo().getName()))
                .map(arb ->
                        bookieName.equals(arb.getBookieOne().getName()) ?
                                arb.getBookieOne() : arb.getBookieTwo()).collect(Collectors.toList());
    }

    protected void testBookieAndSaveStatus(String bookieName, List<Arb> arbs) {

        List<Arb.Bookie> oddsForBookie = getOddsForBookie(arbs, bookieName);

        if(oddsForBookie.size() == 0){
            statuses.put(bookieName, BookieStatus.UNABLE_TO_CHECK);
            return;
        }

        statuses.put(oddsForBookie.get(0).getName(), testBookie(oddsForBookie.get(0)));
    }

    protected BookieStatus testBookie(Arb.Bookie bookie) {

        //TODO: Actually handle these exceptions instead of just printing.

        // Get BookieDriver instance by name.
        BookieDriver bookieDriver = null;
        try {
            bookieDriver = BookieDriverRegistry.getDriver(bookie.getName());
        } catch (BookieDriverNotFoundException e) {
            e.printStackTrace();
        }

        // Get run createBet
        try {
            bookieDriver.createBet(bookie.getCategory(), bookie.getSubCategory(), bookie.getTeamOneName(),
                    bookie.getTeamTwoName(), bookie.getOddType(), 24.0, bookie.getOdd());
        } catch (UnknownOddTypeException e) {
            e.printStackTrace();
            return BookieStatus.UNABLE_TO_CHECK;

        } catch (Throwable e){
            e.printStackTrace();
            return BookieStatus.FAILS;
        }

        return BookieStatus.WORKS;
    }

    /*public void testAdjaraBet(Arb.Bookie bookie) {
        BookieDriver bookieDriver = new AdjaraBetDriver(new ChromeDriver());
    }*/
}
