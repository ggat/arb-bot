package ge.shitbot.bot.drivers;

import ge.shitbot.bot.exceptions.UnknownOddTypeException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;

/**
 * Created by giga on 9/13/17.
 */
public abstract class AbstractBookieDriver implements Closeable {

    protected WebDriver webDriver;
    protected static Logger logger = LoggerFactory.getLogger(AbstractBookieDriver.class);

    public AbstractBookieDriver(WebDriver webDriver){
        this.webDriver = webDriver;
        webDriver.manage().window().maximize();
    }

    public void createBet(String category, String subCategory, String teamOneName, String teamTwoName, String oddType,
                          Double amount, Double oddConfirmation) throws UnknownOddTypeException, RuntimeException {

        category = category.trim();
        subCategory = subCategory.trim();
        teamOneName = teamOneName.trim();
        teamTwoName = teamTwoName.trim();
        oddType = oddType.trim();

        //FIXME: Tmp this is required due to Vakho failure.
        category = category.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");
        subCategory = subCategory.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");

        try {
            createBetImpl(category, subCategory, teamOneName, teamTwoName, oddType, amount, oddConfirmation);
        } catch (Throwable e) {
            logger.error("Error while creating bet - making stakes: {} ", e);
            throw new RuntimeException("Error while creating bet", e);
        }
    }

    protected abstract void createBetImpl(String category, String subCategory, String teamOneName, String teamTwoName,
                                          String oddType, Double amount, Double oddConfirmation) throws UnknownOddTypeException;

    public void close() {
        webDriver.close();
    }
}
