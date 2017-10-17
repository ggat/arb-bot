package ge.shitbot.bot.drivers.bookies;

import ge.shitbot.bot.drivers.BookieDriver;
import ge.shitbot.bot.drivers.BookieDriverGeneral;
import ge.shitbot.bot.exceptions.UnknownOddTypeException;
import ge.shitbot.bot.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Arrays;

/**
 * Created by giga on 9/13/17.
 */
public class CrocoBetDriver extends BookieDriverGeneral implements BookieDriver {

    String baseUrl = "https://www.crocobet.com";
    String user = "ggat";
    String password = "pathfinder48";

    public CrocoBetDriver(WebDriver webDriver) {
        super(webDriver);
    }

    protected void goToMainPage() {

        webDriver.get(baseUrl);
    }

    protected void goToAccountPage() {

        if(!isLoggedIn()) {
            login();
        }

        WebElement accountLink = presenceOfElementLocated(By.xpath("//*[@id=\"navbar\"]/div/a[contains(@href, '/userPanel/deposit')]"));

        accountLink.click();
    }

    protected void login() {

        goToMainPage();

        //Input user name
        webDriver.findElement(By.xpath("//*[@id=\"navbar\"]/form[contains(@name, 'loginForm')]/div/div/input[contains(@name, \"login\")]")).sendKeys(user);
        webDriver.findElement(By.xpath("//*[@id=\"navbar\"]/form[contains(@name, 'loginForm')]/div/div//input[contains(@name, 'password') and contains(@type, \"password\")]")).sendKeys(password);
        webDriver.findElement(By.xpath("//*[@id=\"navbar\"]/form[contains(@name, 'loginForm')]//button[@type='submit']")).click();

    }

    public boolean isLoggedIn() {

        goToMainPage();

        try{
            webDriver.findElement(By.xpath("//*[@id=\"navbar\"]/div/button[@type='button' and contains(@class, 'logout-btn')]")).isDisplayed();
        } catch (Throwable e) {
            return false;
        }

        return true;
    }

    public void createBet(String category, String subCategory, String teamOneName, String teamTwoName, String oddType, Double amount, Double oddConfirmation) throws UnknownOddTypeException {

        //FIXME: If team names are too short or empty it will match lot of odd rows, most probably first row will be selected
        //FIXME: Currently we choose odds using td index which may change in future.
        //TODO: Add event date confirmation
        //TODO: Add odd confirmation.*/

        int oddTypeIndex = getOddTypeIndex(oddType) + 3;

        if(!isLoggedIn()) {
            login();
        }

        //nav სპორტი
        //NOTE: string(.) is used to get entire child nodes as string.
        presenceOfElementLocated(By.xpath("/html/body/div/div[contains(@data-ng-include, 'navigation')]/div/nav/ul/li/a[@href='sports']")).click();

        //aside ფეხბურთი
        presenceOfElementLocated(By.xpath("/html/body/div/div[contains(@class, 'main-content')]//div[contains(@class, 'sport-categories-box')]/div/ul[contains(@class, 'sport-list')]/li[contains(concat(' ', @class, ' '), 'sport-1')]")).click();


        //TODO: Here we should go deeper and ten return up. cause there may be tow ესპანეთი.
        //aside ფეხბურთი
        /*WebElement webElement = */presenceOfElementLocated(By.xpath("/html/body/div/div[contains(@class, 'main-content')]//div[contains(@class, 'sport-categories-box')]/div/div/ul[havingClass('sport-list') and havingClass('subcategory')]//span[havingClass('categoryName') and contains(string(.), 'ესპანეთი')]/ancestor::li[1]")).click();
        /*WebElement subCategoryMenu = */presenceOfElementLocated(By.xpath("//*[contains(@id, 'categoryId_') and not(contains(@style, 'display: none'))]/li/span[contains(string(.), 'ლა ლიგა')]/ancestor::li[1]")).click();

        //Specific event row
        presenceOfElementLocated(By.xpath("//*[@id=\"sport-content\"]//div[contains(@class, 'country-level')]//div[contains(@class, 'panel-body')]//div[contains(@class, 'league-level')]//span[contains(string(.), 'ესპანეთი') and contains(string(.), 'ლა ლიგა') ]/ancestor::div[contains(@class, 'league-level')]/following-sibling::div//div[contains(@class, 'panel-body')]/ul/li[contains(@class, 'single-event')]//li[contains(@class, 'period-item')]/div[havingClass('event') and havingClass('name') and contains(string(.), 'ლევანტე') and contains(string(.), 'ხეტაფე')]")).click();

        //NOTE: After this line we need to finish.

        //actually picking a bet
        presenceOfElementLocated(By.xpath("//*[@id=\"Sport27\"]/div/div[contains(@class, 'games-container')]/div/div[contains(@class, 'collapsible-header')]/h3[contains(string(.), '"+ subCategory +"') and contains(string(.), '" + category +  "')]/parent::div/following-sibling::div[contains(@class, 'collapsible-body')]//tbody/tr/td[contains(@class, 'cell-pair') and contains(string(.), '"+teamOneName+"') and contains(string(.), '"+teamTwoName+"')]/parent::tr/td["+oddTypeIndex+"]")).click();

        WebElement stakeInput = presenceOfElementLocated(By.xpath("//*[@id=\"StakeValue\"]"));

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

    public Long getBalance() {

        goToAccountPage();

        WebElement balanceElement = presenceOfElementLocated(By.xpath("/html/body/div/div//div[@data-balance]//li/span[@class='total']"));

        String rawBalance = balanceElement.getText().trim().replaceAll("GEL", "").replaceAll(",", ".").trim();

        return Math.round(Double.parseDouble(rawBalance) * 100);
    }
}
