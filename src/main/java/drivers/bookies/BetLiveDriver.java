package drivers.bookies;

import drivers.AbstractBookieDriver;
import drivers.BookieDriver;
import exceptions.BookieDriverException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;

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

    public void createBet(String category, String subcategory, String teamOneName, String teamTwoName, Double amount, Double oddConfirmation) {

        login();

        WebElement navSport = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/header/nav/ul/li/a[contains(text(), 'სპორტი')]")));

        navSport.click();


        webDriver.switchTo().frame("sport");

        WebElement asideFotball = null;
        try {
            asideFotball = (new WebDriverWait(webDriver, 10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='left_aside']/div[contains(@class, 'left_menu_new')]/div[contains(@class, 'sport_items')]/div//span[contains(text(), 'ფეხბურთი')]/parent::span/parent::div")));
        } catch (Exception e) {


            asideFotball = (new WebDriverWait(webDriver, 10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='left_aside']")));
        }

        asideFotball.click();

        WebElement country = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id='country_div']/tbody//div[contains(@class, 'country_name') and contains(@title, '"+ category +"')]/following-sibling::div/div[contains(@class, 'country_item') and contains(@title, '"+subcategory+"')]")));

        country.click();

        String rowSelector = "//*[@id='main_grid_container']/div[2]/div[1]/div[2]/table/tbody/tr//span[contains(@class, 'event_name') and contains(@title, '"+teamOneName+"') and contains(@title, '"+teamTwoName+"')]/parent::td/parent::tr";

        WebElement row = (new WebDriverWait(webDriver, 10))
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath(rowSelector)));

        //TODO: check if this date matches to our date.
        String date = row.findElement(By.xpath("//td[contains(@class, 'lg_date')]")).getText();

        HashMap<String, String> oddTypeMatchers = new HashMap<>();

        oddTypeMatchers.put("1", "1");
        oddTypeMatchers.put("X", "X");
        oddTypeMatchers.put("2", "2");

        //TODO: We shoul find odd type <TD> positions to ensure they have same indexes as before.
        String headerRowSelector = "//*[@id=\"main_grid_container\"]/div[2]/div[1]/div[2]/table/tbody/tr[contains(@class, 'lg_table_row') and contains(@class, 'header')]";

        row.findElement(By.xpath("//*[@id='main_grid_container']/div[2]/div[1]/div[2]/table/tbody/tr//span[contains(@class, 'event_name') and contains(@title, '"+teamOneName+"') and contains(@title, '"+teamTwoName+"')]/parent::td/parent::tr/td[3]")).click();

        WebElement odd = row.findElement(By.xpath("//td[3]"));
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        odd.click();

        //row.click();
    }

    public void close(){
        webDriver.close();
    }
}
