package ge.arb-bot.gui.service;

import ge.arb-bot.bot.drivers.BookieDriver;
import ge.arb-bot.bot.drivers.BookieDriverRegistry;
import ge.arb-bot.bot.exceptions.BookieDriverNotFoundException;
import ge.arb-bot.bot.exceptions.UnknownOddTypeException;
import ge.arb-bot.core.datatypes.Arb;

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
