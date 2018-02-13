package ge.shitbot.bot.drivers;

import ge.shitbot.bot.exceptions.UnknownOddTypeException;

import java.io.Closeable;

/**
 * Created by giga on 9/13/17.
 */
public interface BookieDriver extends Closeable {

    Long getBalance() throws Throwable;
    boolean isLoggedIn() throws Throwable;
    void createBet(String category, String subCategory, String teamOneName, String teamTwoName, String oddType,
                   Double amount, Double oddConfirmation) throws UnknownOddTypeException, RuntimeException;

}
