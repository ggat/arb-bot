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
public class CrystalBetDriver extends AbstractBookieDriver implements BookieDriver {

    String baseUrl = "https://www.crystalbet.com/Pages/StartPage.aspx";
    String user = "zurabinio";
    String password = "zuraba1234";

    public CrystalBetDriver(WebDriver webDriver) {
        super(webDriver);
    }

    protected void goToMainPage() {

        webDriver.get(baseUrl);
    }

    protected void goToAccountPage() {

        if(!isLoggedIn()) {
            login();
        }

        WebElement accountLink = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"ctl00_UpdatePanelLogin\"]/a[contains(@class, 'myaccaunt')]")));

        accountLink.click();
    }

    protected void login() {

        goToMainPage();

        //Input user name
        webDriver.findElement(By.cssSelector("#ctl00_MainLoginView_MainLogin_UserName")).sendKeys(user);
        webDriver.findElement(By.cssSelector("#ctl00_MainLoginView_MainLogin_Password")).sendKeys(password);
        webDriver.findElement(By.cssSelector("#ctl00_MainLoginView_MainLogin_LoginButton")).click();

    }

    public boolean isLoggedIn(){

        try{
            webDriver.findElement(By.xpath("//*[@id=\"ctl00_MainLoginView_MainLoginStatus\"]")).isDisplayed();
        } catch (Throwable e) {
            return false;
        }

        return true;

    }

    public Long getBalance() {

        goToAccountPage();

        WebElement balanceElement = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"LabelSportsBalance\"]")));

        String rawBalance = balanceElement.getText().trim().replaceAll("GEL", "").replaceAll(",", ".").trim();

        return Math.round(Double.parseDouble(rawBalance) * 100);
    }
}
