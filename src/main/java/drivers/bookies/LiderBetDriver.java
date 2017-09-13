package drivers.bookies;

import drivers.AbstractBookieDriver;
import drivers.BookieDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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

    public Long getBalance() {

        login();

        return 0L;
    }
}
