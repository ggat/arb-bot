package drivers.bookies;

import drivers.AbstractBookieDriver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.DecimalFormat;

/**
 * Created by giga on 10/10/17.
 */
public class BookieDriverGeneral extends AbstractBookieDriver {

    public BookieDriverGeneral(WebDriver webDriver) {
        super(webDriver);
    }

    protected WebElement presenceOfElementLocated(By by) {
        return presenceOfElementLocated(10L, by);
    }
    protected WebElement presenceOfElementLocated(Long timeOut, By by) {
        return (new WebDriverWait(webDriver, timeOut))
                .until(ExpectedConditions.presenceOfElementLocated(by));
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
