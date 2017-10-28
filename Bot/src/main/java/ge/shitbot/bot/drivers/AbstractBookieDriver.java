package ge.shitbot.bot.drivers;

import ge.shitbot.bot.exceptions.UnknownOddTypeException;
import org.openqa.selenium.WebDriver;

import java.io.Closeable;

/**
 * Created by giga on 9/13/17.
 */
public abstract class AbstractBookieDriver implements Closeable {

    protected WebDriver webDriver;

    public AbstractBookieDriver(WebDriver webDriver){
        this.webDriver = webDriver;
        webDriver.manage().window().maximize();
    }

    public void createBet(String category, String subCategory, String teamOneName, String teamTwoName, String oddType,
                          Double amount, Double oddConfirmation) throws UnknownOddTypeException {

        category = category.trim();
        subCategory = subCategory.trim();
        teamOneName = teamOneName.trim();
        teamTwoName = teamTwoName.trim();
        oddType = oddType.trim();

        //FIXME: Tmp this is required due to Vakho failure.
        category = category.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");
        subCategory = subCategory.replace("\r\n", " ").replace("\n", " ").replace("\r", " ");

        createBetImpl(category, subCategory, teamOneName, teamTwoName, oddType, amount, oddConfirmation);
    }

    protected abstract void createBetImpl(String category, String subCategory, String teamOneName, String teamTwoName,
                                          String oddType, Double amount, Double oddConfirmation) throws UnknownOddTypeException;

    public void close() {
        webDriver.close();
    }
}
