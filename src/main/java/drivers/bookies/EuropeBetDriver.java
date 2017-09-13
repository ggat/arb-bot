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
public class EuropeBetDriver extends AbstractBookieDriver implements BookieDriver {

    String baseUrl = "https://www.europebet.com/ka";
    String user = "i.gatenashvili";
    String password = "axalgori11";

    public EuropeBetDriver(WebDriver webDriver) {
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
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"mainheader\"]/div/div/div[2]/form/ul/li[@data-itemclicked='Account']")));

        accountLink.click();
    }

    protected void login() {

        goToMainPage();

        // Sometimes inputs are not show directly and we need to go with popup
        try {
            //Input user name
            webDriver.findElement(By.xpath("//*[@id=\"main-loginform\"]/span[1]/span[1]/input")).sendKeys(user);
            webDriver.findElement(By.xpath("//*[@id=\"main-loginform\"]/span[1]/span[2]/input")).sendKeys(password);
            webDriver.findElement(By.xpath("//*[@id=\"main-loginform\"]/span[2]/button")).click();
        } catch (Throwable e) {

            // Click on 'შესვლა' button to open modal window
            webDriver.findElement(By.cssSelector("#btnLogin")).click();

            //Wait for it
            WebElement modal = (new WebDriverWait(webDriver, 10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#BCore-ModalDialog-Content-LoginWindow")));

            WebElement userInput = (new WebDriverWait(webDriver, 10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#Username")));

            //Input credentials and login
            userInput.sendKeys(user);
            modal.findElement(By.cssSelector("#Password")).sendKeys(password);
            modal.findElement(By.cssSelector("#modalLoginButton")).click();
        }

    }

    public boolean isLoggedIn() {
        goToMainPage();

        try{
            webDriver.findElement(By.xpath("//*[@id=\"mainheader\"]/div/div/div[2]/form/span/button[@data-itemclicked='Logout']")).isDisplayed();
        } catch (Throwable e) {
            return false;
        }

        return true;
    }

    public Long getBalance() {

        goToAccountPage();

        String stringAmount = webDriver.findElement(By.xpath("//*[@id=\"myaccountcontent\"]/div/div[2]/ul/div" +
                "/div[1]/div/div[2]/div/div[contains(@class, 'bonustamount')]")).getText().trim().replaceAll(",", ".");

        Double balance = Double.parseDouble(stringAmount);

        //Long shiftedBalance = (long) (balance * 100);

        return Math.round(Double.parseDouble(stringAmount) * 100);
    }
}
