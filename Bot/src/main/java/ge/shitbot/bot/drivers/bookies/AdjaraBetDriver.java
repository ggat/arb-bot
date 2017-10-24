package ge.shitbot.bot.drivers.bookies;

import ge.shitbot.bot.OddType;
import ge.shitbot.bot.drivers.BookieDriver;
import ge.shitbot.bot.drivers.BookieDriverGeneral;
import ge.shitbot.bot.exceptions.UnknownOddTypeException;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Arrays;

/**
 * Created by giga on 9/13/17.
 */
public class AdjaraBetDriver extends BookieDriverGeneral implements BookieDriver {

    String baseUrl = "https://www.adjarabet.com/ka";
    String user = "ggat";
    String password = "Boogieman50";

    public AdjaraBetDriver(WebDriver webDriver) {
        super(webDriver);

        // Sometimes there is a popup. That is restricting us to click on it.
        // This is attempt to remove it.
        try {
            JavascriptExecutor js;
            if (webDriver instanceof JavascriptExecutor) {
                js = (JavascriptExecutor) webDriver;
                js.executeScript("return document.getElementsByClassName('pop-up-root').remove();");
            }
        } catch (Exception e) {
            //e.printStackTrace();
        }
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

    protected void createBetImpl(String category, String subCategory, String teamOneName, String teamTwoName,
                                 String oddType, Double amount, Double oddConfirmation) throws UnknownOddTypeException {

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
        presenceOfElementLocated(By.xpath("/html/body/my-app/my-header/div/adj-list/a[contains(string(.), 'სპორტი')]")).click();

        //aside ფეხბურთი
        presenceOfElementLocated(By.xpath("//*[@id=\"Cat27\" and contains(string(.), 'ფეხბურთი')]")).click();


        //TODO: Here we should go deeper and ten return up. cause there may be tow ესპანეთი.
        //aside ფეხბურთი
        presenceOfElementLocated(By.xpath("//*[@id=\"Cat27\" and contains(string(.), 'ფეხბურთი')]/following-sibling::div/ul/li/a/span[contains(@class, 'category') and contains(text(), '"+ category +"')]")).click();

        //aside ფეხბურთი
        presenceOfElementLocated(By.xpath("//*[@id=\"Cat27\" and contains(string(.), 'ფეხბურთი')]/following-sibling::div/ul/li/a/span[contains(@class, 'category') and contains(text(), '"+ category +"')]/parent::a/following-sibling::ul/li/a[contains(text(), '"+ subCategory +"')]")).click();

        //actually picking a bet
        presenceOfElementLocated(By.xpath("//*[@id=\"Sport27\"]/div/div[contains(@class, 'games-container')]/div/div[contains(@class, 'collapsible-header')]/h3[contains(string(.), '"+ subCategory +"') and contains(string(.), '" + category +  "')]/parent::div/following-sibling::div[contains(@class, 'collapsible-body')]//tbody/tr/td[contains(@class, 'cell-pair') and contains(string(.), '"+teamOneName+"') and contains(string(.), '"+teamTwoName+"')]/parent::tr/td["+oddTypeIndex+"]")).click();

        WebElement stakeInput = presenceOfElementLocated(By.xpath("//*[@id=\"StakeValue\"]"));

        stakeInput.clear();

        stakeInput.sendKeys(presentDouble(amount));

    }

    //Non-zero based index
    protected int getOddTypeIndex(String oddType) throws UnknownOddTypeException {

        String[] arr = {
                OddType.Win,
                OddType.Draw,
                OddType.Loose,
                OddType.WinOrDraw,
                OddType.WinOrLoose,
                OddType.DrawOrLoose,
                "",
                "",
                OddType.Under25,
                OddType.Over25,
                OddType.Yes,
                OddType.No
        };

        int index = Arrays.asList(arr).indexOf(oddType);

        if( index == -1 ) {
            throw new UnknownOddTypeException("Odd type [" + oddType + "]");
        }

        return index + 1;
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
