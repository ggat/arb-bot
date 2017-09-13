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
public class AdjaraBetDriver extends AbstractBookieDriver implements BookieDriver {

    String baseUrl = "https://www.adjarabet.com/ka";
    String user = "ggat";
    String password = "Boogieman50";

    public AdjaraBetDriver(WebDriver webDriver) {
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
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/my-app/my-header/div/adj-login/div/button[contains(text(), 'ანგარიში')]")));

        accountLink.click();
    }

    protected void login() {

        goToMainPage();

        //Input user name
        webDriver.findElement(By.xpath("//*[@id=\"username\"]")).sendKeys(user);
        webDriver.findElement(By.xpath("//*[@id=\"password\"]")).sendKeys(password);
        webDriver.findElement(By.xpath("/html/body/my-app/my-header/div/adj-login/div/form/button[@type='submit']")).click();

    }

    public boolean isLoggedIn() {

        goToMainPage();

        try{
            webDriver.findElement(By.xpath("/html/body/my-app/my-header/div/adj-login/div/button[contains(text(), 'გასვლა')]")).isDisplayed();
        } catch (Throwable e) {
            return false;
        }

        return true;
    }

    public Long getBalance() {

        goToAccountPage();

        WebElement balanceElement = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/my-app/my-template/my-template/my-template/" +
                        "div/adj-balance/div/div[2]/form[1]/div[contains(@class, 'balance__amount')]")));

        String rawBalance = balanceElement.getText().trim().replaceAll("GEL", "").replaceAll(",", ".").trim();

        return Math.round(Double.parseDouble(rawBalance) * 100);
    }
}
