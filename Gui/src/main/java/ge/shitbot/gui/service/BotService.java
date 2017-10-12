package ge.shitbot.gui.service;

import drivers.BookieDriver;
import drivers.BookieDriverRegistry;
import exceptions.BookieDriverNotFoundException;
import exceptions.UnknownOddTypeException;
import ge.shitbot.datasources.datatypes.Arb;

/**
 * Created by giga on 10/12/17.
 */
public class BotService {

    public void createBet(Arb.Bookie bookie, Double amount) throws BookieDriverNotFoundException, UnknownOddTypeException {

        BookieDriver bookieDriver = BookieDriverRegistry.getDriver(bookie.getName());

        bookieDriver.createBet(bookie.getCategory(), bookie.getSubCategory(), bookie.getTeamOneName(),
                bookie.getTeamTwoName(), bookie.getOddType(), amount, bookie.getOdd());
    }
}
