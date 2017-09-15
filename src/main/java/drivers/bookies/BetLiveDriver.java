package drivers.bookies;

import drivers.AbstractBookieDriver;
import drivers.BookieDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

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

    protected void goToAccountPage() {

        if(!isLoggedIn()) {
            login();
        }

        WebElement accountLink = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/header/div/div[1]/div[1]/div[3]/a/span[contains(text(), 'ბალანსის მარ')]")));

        accountLink.click();
    }

    protected void login() {

        goToMainPage();

        //Input user name
        webDriver.findElement(By.cssSelector("#username")).sendKeys(user);
        webDriver.findElement(By.cssSelector("#password")).sendKeys(password);
        webDriver.findElement(By.xpath("/html/body/header/div/div[1]/form//*[@type='submit']")).click();
    }

    public boolean isLoggedIn() {
        try{
            webDriver.findElement(By.xpath("/html/body/header/div/div[1]/div[1]/div[3]/a/span[contains(text(), 'ბალანსის მარ')]")).isDisplayed();
        } catch (Throwable e) {
            return false;
        }

        return true;
    }

    public Long getBalance() {

        login();

        WebElement balanceElement = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/header/div/div[1]/div[1]/div[2]/div[3]/span[contains(@class, 'userBalanceVal')]")));

        String rawBalance = balanceElement.getText().trim().replaceAll("GEL", "").replaceAll(",", ".").trim();

        return Math.round(Double.parseDouble(rawBalance) * 100);
    }
}
