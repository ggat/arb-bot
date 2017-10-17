package ge.shitbot.bot.drivers.bookies;

import ge.shitbot.bot.drivers.BookieDriver;
import ge.shitbot.bot.drivers.BookieDriverGeneral;
import ge.shitbot.bot.exceptions.UnknownOddTypeException;
import ge.shitbot.bot.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;

/**
 * Created by giga on 9/13/17.
 */
public class BetLiveDriver extends BookieDriverGeneral implements BookieDriver {

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
                .until(ExpectedConditions.presenceOfElementLocated(By.xpath("/html/body/header/div/div[1]/div[1]/div[2]/div[3]/span[hasClass('userBalanceVal')]")));

        String rawBalance = balanceElement.getText().trim().replaceAll("GEL", "").replaceAll(",", ".").trim();

        return Math.round(Double.parseDouble(rawBalance) * 100);
    }

    public void createBet(String category, String subcategory, String teamOneName, String teamTwoName, String oddType, Double amount, Double oddConfirmation) throws UnknownOddTypeException {

        int oddTypeIndex = getOddTypeIndex(oddType);

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

        //TODO: check if this date matches to our date.
        String date = presenceOfElementLocated(By.xpath(rowSelector + "/td[contains(@class, 'lg_date')]")).getText();

        WebElement odd = presenceOfElementLocated(By.xpath(rowSelector + "/td[contains(@class, 'oddItem')]["+ oddTypeIndex +"]"));
        try {
            Thread.sleep(3000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        odd.click();

        WebElement stakeInput = presenceOfElementLocated(By.xpath("//*[@id=\"betslip_control\"]/div[contains(@class, 'betslip_stake') and contains(string(.), 'ფსონი')]/input"));
        stakeInput.clear();
        stakeInput.sendKeys(presentDouble(amount));
    }

    //Non-zero based index
    protected int getOddTypeIndex(String oddType) throws UnknownOddTypeException {

        String[] arr = {"1", "", "2", "1X", "", "X2", "", "", "", "", "Yes", "No"};

        int index = Arrays.asList(arr).indexOf(oddType);

        if( index == -1 ) {
            throw new UnknownOddTypeException("Odd type [" + oddType + "]");
        }

        return index + 1;
    }

    public void close(){
        webDriver.close();
    }
}
