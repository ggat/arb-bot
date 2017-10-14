package ge.shitbot.gui.service;

import ge.shitbot.bot.drivers.BookieDriver;
import ge.shitbot.bot.drivers.BookieDriverRegistry;
import ge.shitbot.bot.exceptions.BookieDriverNotFoundException;
import ge.shitbot.bot.exceptions.UnknownOddTypeException;
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
