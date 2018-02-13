package ge.shitbot.bot.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

/**
 * Created by giga on 2/13/18.
 */
public class Combinations {

    public static void hoverAndClick(WebDriver webDriver, WebElement webElement) {
        Actions actions = new Actions(webDriver);
        actions.moveToElement(webElement).click().build().perform();
    }
}
