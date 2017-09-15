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
public class LiderBetDriver extends AbstractBookieDriver implements BookieDriver {

    String baseUrl = "https://www.lider-bet.com/web/ka/promotions/?page=sagzuri";
    String user = "i.gatenashvili";
    String password = "camaross69";

    public LiderBetDriver(WebDriver webDriver) {
        super(webDriver);
    }

    protected void goToMainPage() {

        webDriver.get(baseUrl);
    }

    protected void login() {

        goToMainPage();

        //Input user name
        webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/table/tbody/tr/td[2]/span/div/div[2]/input")).sendKeys(user);
        webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/table/tbody/tr/td[2]/span/div/div[3]/input")).sendKeys(password);
        webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/table/tbody/tr/td[2]/span/div//*[@onclick='auth_submiter(this)']")).click();

    }

    public boolean isLoggedIn() {
        goToMainPage();

        try{
            webDriver.findElement(By.xpath("/html/body/div[1]/div[2]/div/table/tbody/tr/td[2]/div/div[5]/a[@class='my_room']")).isDisplayed();
        } catch (Throwable e) {
            return false;
        }

        return true;
    }

    public Long getBalance() {

        login();

        WebElement balanceElement = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/div[1]/div[2]/div/table/tbody/tr/td[2]/div/div[4]/a[@class='user-amount-block']")));

        String rawBalance = balanceElement.getText().trim().replaceAll("GEL", "").replaceAll(",", ".").trim();

        return Math.round(Double.parseDouble(rawBalance) * 100);
    }
}
