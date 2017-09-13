package drivers.bookies;

import drivers.AbstractBookieDriver;
import drivers.BookieDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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

    protected void login() {

        goToMainPage();

        //Input user name
        webDriver.findElement(By.cssSelector("#ctl00_MainLoginView_MainLogin_UserName")).sendKeys(user);
        webDriver.findElement(By.cssSelector("#ctl00_MainLoginView_MainLogin_Password")).sendKeys(password);
        webDriver.findElement(By.cssSelector("#ctl00_MainLoginView_MainLogin_LoginButton")).click();

    }

    public Long getBalance() {

        login();

        return 0L;
    }
}
