package ge.shitbot.gui.service;

import drivers.BookieDriver;
import drivers.BookieDriverRegistry;
import exceptions.BookieDriverNotFoundException;
import ge.shitbot.datasources.datatypes.Arb;

/**
 * Created by giga on 10/12/17.
 */
public class BotService {

    public void createBet(Arb.Bookie bookie, Double amount) throws BookieDriverNotFoundException {

        BookieDriver bookieDriver = BookieDriverRegistry.getDriver(bookie.getName());

        bookieDriver.createBet(bookie.getCategory(), bookie.getSubCategory(), bookie.getTeamOneName(),
                bookie.getTeamTwoName(), amount, bookie.getOdd());
    }
}
