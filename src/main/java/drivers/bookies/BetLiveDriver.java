package drivers.bookies;

import drivers.AbstractBookieDriver;
import drivers.BookieDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Created by giga on 9/13/17.
 */
public class BetLiveDriver extends AbstractBookieDriver implements BookieDriver {

    String baseUrl = "https://www.betlive.com/ka";
    String user = "i.gatenashvili";
    String password = "axalgori11";

    public BetLiveDriver(WebDriver webDriver) {
        super(webDriver);
        webDriver.manage().window().maximize();
    }

    protected void goToMainPage() {

        webDriver.get(baseUrl);
    }

    protected void login() {

        goToMainPage();

        //Input user name
        webDriver.findElement(By.cssSelector("#username")).sendKeys(user);
        webDriver.findElement(By.cssSelector("#password")).sendKeys(password);
        webDriver.findElement(By.xpath("/html/body/header/div/div[1]/form//*[@type='submit']")).click();
    }

    public Long getBalance() {

        login();

        return 0L;
    }
}
