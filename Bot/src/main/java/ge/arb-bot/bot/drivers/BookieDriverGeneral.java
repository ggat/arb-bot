package ge.arb-bot.bot.drivers;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by giga on 10/10/17.
 */
public abstract class BookieDriverGeneral extends AbstractBookieDriver {

    public BookieDriverGeneral(WebDriver webDriver) {
        super(webDriver);
    }

    protected WebElement presenceOfElementLocated(By by) {
        return presenceOfElementLocated(10L, by);
    }

    protected WebElement presenceOfElementLocated(Long timeOut, By by) {
        WebElement element = (new WebDriverWait(webDriver, timeOut))
                .until(ExpectedConditions.presenceOfElementLocated(by));

        //webDriver.manage().timeouts().setScriptTimeout(10, TimeUnit.SECONDS);

        return element;
    }

    protected WebElement visibilityOfElementLocated(Long timeOut, By by) {
        return (new WebDriverWait(webDriver, timeOut))
                .until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    protected void hoverAndClick(WebElement webElement) {
        Actions actions = new Actions(webDriver);
        actions.moveToElement(webElement, 1, 1).click().build().perform();
    }

    protected WebElement visibilityOfElementLocated(By by) {
        return visibilityOfElementLocated(10L, by);
    }

    protected void ensureClick(By by) {
        ensureClick(by, 1500L);
    }

    protected void ensureClick(By by, Long timeout) {
        try {
            // nav სპორტი
            WebElement element = presenceOfElementLocated(by);

            try {
                Thread.sleep(timeout);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            element.click();

            // It seems we find element before page refreshes after login and then it not in current DOM anymore
            // so we try to find it again.
        } catch (StaleElementReferenceException e) {

            presenceOfElementLocated(by).click();
        }
    }

    protected void windowScroll(Long x, Long y) {
        JavascriptExecutor jse = (JavascriptExecutor)webDriver;
        jse.executeScript("window.scrollBy(" + x + ", " + y + ")", "");
    }

    protected String presentDouble(Double value){
        return (new DecimalFormat("#0.00")).format(value);
    }
}
