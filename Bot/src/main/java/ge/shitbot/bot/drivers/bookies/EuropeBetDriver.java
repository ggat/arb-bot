package ge.shitbot.bot.drivers.bookies;

import ge.shitbot.bot.drivers.BookieDriver;
import ge.shitbot.bot.drivers.BookieDriverGeneral;
import ge.shitbot.bot.exceptions.UnknownOddTypeException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;

/**
 * Created by giga on 9/13/17.
 */
public class EuropeBetDriver extends BookieDriverGeneral implements BookieDriver {

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

        try {
            WebElement accountLink = (new WebDriverWait(webDriver, 10))
                    .until(ExpectedConditions.presenceOfElementLocated(By.xpath("//*[@id=\"mainheader\"]/div/div/div[2]/form/ul/li[@data-itemclicked='Account']")));

            accountLink.click();

        } catch (Throwable e) { //Sometimes we have dropdown to access account and this must be a case.

            WebElement accountDropDown = (new WebDriverWait(webDriver, 10))
                    .until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"gAccount\"]/div[contains(@class, 'dropdown')]")));

            accountDropDown.click();

            WebElement balanceLink = (new WebDriverWait(webDriver, 10))
                    .until(ExpectedConditions.presenceOfElementLocated(
                            By.xpath("//*[@id=\"gAccount\"]//a/div/span[contains(text(), 'ბალანსი')]")));

            balanceLink.click();
        }
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

            try {
                // Click on 'შესვლა' button to open modal window
                webDriver.findElement(By.cssSelector("#btnLogin")).click();

                //Wait for it
                WebElement modal = (new WebDriverWait(webDriver, 10))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#BCore-ModalDialog-Content-iframe-LoginWindow")));

                webDriver.switchTo().frame("BCore-ModalDialog-Content-iframe-LoginWindow");

                WebElement userInput = (new WebDriverWait(webDriver, 10))
                        .until(ExpectedConditions.presenceOfElementLocated(By.id("Username")));

                //Input credentials and login
                userInput.sendKeys(user);
                webDriver.findElement(By.cssSelector("#Password")).sendKeys(password);
                webDriver.findElement(By.cssSelector("#modalLoginButton")).click();

                webDriver.switchTo().defaultContent();

            } catch (Exception e1) {
                e1.printStackTrace();
            }
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

        return Math.round(Double.parseDouble(stringAmount) * 100);
    }

    public void createBet(String category, String subCategory, String teamOneName, String teamTwoName, String oddType,
                          Double amount, Double oddConfirmation) throws UnknownOddTypeException {

        //FIXME: If team names are too short or empty it will match lot of odd rows, most probably first row will be selected
        //FIXME: Currently we choose odds using td index which may change in future.
        //TODO: Add event date confirmation
        //TODO: Add odd confirmation.*/

        int oddTypeIndex = getOddTypeIndex(oddType);

        if(!isLoggedIn()) {
            login();
        }

        ensureClick(By.xpath("//*[@id=\"site\"]/header/nav/div/div/ul[contains(@class, 'topmenu')]/li/a[contains(text(), 'სპორტი')]"));

        webDriver.switchTo().frame("gameIFrame");

        WebElement sportCategoryButton = presenceOfElementLocated(By.xpath("//*[@id=\"select-sports-panel-placeholder\"]/div[contains(@class, 'select-sports-panel')]//div/span[contains(@class, 'sport-name') and contains(text(), 'ფეხბურთი')]/ancestor::div[contains(@class, 'subcategory-buttons-row')][1]/div"));

        // Sport category may be already selected
        if( ! sportCategoryButton.getAttribute("class").contains("active")){
            sportCategoryButton.click();
        }

        //Final span is important here we must click on span instead of its div with class cat3-row*, cause click on div may not work.
        presenceOfElementLocated(By.xpath("//*[@id=\"category-tree-container-1\"]/div/div[contains(@class, 'category-container')]/div[contains(@class, 'category2-title pointer') and contains(string(.), '"+ category +"')]/following-sibling::div[contains(@class, 'cat3-row')]/span[contains(string(.), '"+ subCategory +"')]")).click();

        presenceOfElementLocated(By.xpath("//div[@id=\"category-page\"]//div[contains(@class, 'events-table')]//div[contains(@id, 'c-level3-header') and contains(string(.), '" + category + "') and contains(string(.), '" + subCategory + "')]/following-sibling::div[contains(@id, 'c-level3-row')]/div[2][contains(string(.), '"+ teamOneName +"') and contains(string(.), '" + teamTwoName + "')]/following-sibling::div[contains(@class, 'outcome-row')]/div[contains(@id, 'outcome-')][" + oddTypeIndex + "]")).click();

        WebElement stakeInput = presenceOfElementLocated(By.xpath("//*[@id=\"simple-stake\"]"));

        stakeInput.clear();

        stakeInput.sendKeys(presentDouble(amount));

    }

    //Non-zero based index
    protected int getOddTypeIndex(String oddType) throws UnknownOddTypeException {

        String[] arr = {"1", "", "2", "1X", "", "X2", "", "", "Yes", "No"};

        int index = Arrays.asList(arr).indexOf(oddType);

        if( index == -1 ) {
            throw new UnknownOddTypeException("Odd type [" + oddType + "]");
        }

        return index + 1;
    }
}
